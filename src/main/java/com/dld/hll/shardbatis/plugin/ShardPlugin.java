package com.dld.hll.shardbatis.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.dld.hll.shardbatis.ShardException;
import com.dld.hll.shardbatis.anno.ShardingTable;
import com.dld.hll.shardbatis.builder.ShardConfigHolder;
import com.dld.hll.shardbatis.builder.ShardConfigParser;
import com.dld.hll.shardbatis.converter.SqlConverterFactory;
import com.dld.hll.shardbatis.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;


@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ShardPlugin implements Interceptor {
    private static final Log log = LogFactory.getLog(ShardPlugin.class);

    public static final String SHARDING_CONFIG = "shardingConfig";


    private static final ConcurrentHashMap<String, Boolean> cache = new ConcurrentHashMap<String, Boolean>();

    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        MappedStatement mappedStatement = null;
        if (statementHandler instanceof RoutingStatementHandler) {
            StatementHandler delegate = (StatementHandler) ReflectionUtils.getFieldValue(statementHandler, "delegate");
            mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
        } else {
            mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler, "mappedStatement");
        }

        String mapperId = mappedStatement.getId();

        if (isShouldParse(mapperId)) {
            String sql = statementHandler.getBoundSql().getSql();
            if (log.isDebugEnabled()) {
                log.debug("Original Sql [" + mapperId + "]:" + sql);
            }
            Object params = statementHandler.getBoundSql().getParameterObject();

            SqlConverterFactory cf = SqlConverterFactory.getInstance();
            sql = cf.convert(sql, params, mapperId);
            if (log.isDebugEnabled()) {
                log.debug("Converted Sql [" + mapperId + "]:" + sql);
            }
            ReflectionUtils.setFieldValue(statementHandler.getBoundSql(), "sql", sql);
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

    public void setProperties(Properties properties) {
        // 解析配置文件
        String config = properties.getProperty(SHARDING_CONFIG);
        if (StringUtils.isEmpty(config)) {
            throw new IllegalArgumentException(
                    "property 'shardingConfig' is requested.");
        }

        ShardConfigParser parser = new ShardConfigParser();
        try (InputStream input = Resources.getResourceAsStream(config)) {
            parser.parse(input);
        } catch (IOException e) {
            String msg = "Get sharding config file failed.";
            log.error(msg, e);
            throw new ShardException(msg);
        } catch (SAXException e) {
            String msg = "Parse sharding config file failed.";
            log.error(msg, e);
            throw new ShardException(msg);
        } catch (ParserConfigurationException e) {
            String msg = "Parse sharding config file failed.";
            log.error(msg, e);
            throw new ShardException(msg);
        }
    }

    private boolean isShouldParse(String mapperId)  {
        Boolean parse = cache.get(mapperId);

        if (parse != null) {//已被缓存
            return parse;
        }

        String className = mapperId.substring(0, mapperId.lastIndexOf("."));
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        Class<?> clazz = null;

        try {
            Resources.classForName(className);
        } catch (ClassNotFoundException e) {
            throw new ShardException("class not found ");
        }

        Method method = ReflectionUtils.findMethod(clazz.getDeclaredMethods(), methodName);
        /*
         * 1.<selectKey>不做解析
         * 2.在ignoreList里的sql不用处理
         * 3.如果不在ignoreList里并且没有配置parseList则进行处理
         * 4.如果不在ignoreList里并且也在parseList里则进行处理
         * 5.如果不在ignoreList里并且也不在parseList里则不进行处理
         */
        if (method.isAnnotationPresent(ShardingTable.class)) {
            parse = true;
        } else if (!mapperId.endsWith("!selectKey")) {

            ShardConfigHolder configHolder = ShardConfigHolder.getInstance();

            if (!configHolder.isIgnoreId(mapperId)) {
                if (!configHolder.isConfigParseId() || configHolder.isParseId(mapperId)) {
                    parse = true;
                }
            }
        }

        if (parse == null) {
            parse = false;
        }
        cache.put(mapperId, parse);
        return parse;
    }


}
