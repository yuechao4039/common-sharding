财务系统 > 基于mybatis拦截器的分表 > sharding (2).png

1.实现自定义拦截器。拦截在StatementHandler的prepare之前。

package com.dld.hll.shardbatis.plugin;
 
 
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ShardPlugin implements Interceptor {
    private static final Log log = LogFactory.getLog(ShardPlugin.class);
 
    public static final String SHARDING_CONFIG = "shardingConfig";
 
 
......
......


2.在mybatis配置文件中添加配置

<plugins>
    <plugin interceptor="com.dld.hll.shardbatis.plugin.ShardPlugin">
            <property name="shardingConfig" value="shard_config.xml"/>
    </plugin>
</plugins>


3.分表相关配置文件shard_config.xml，用于配置分表策略，哪些Mapper的方法的参与分表。此文件从classpath中查找。
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE shardingConfig PUBLIC "-//shardbatis.googlecode.com//DTD Shardbatis 2.0//EN"
 "http://shardbatis.googlecode.com/dtd/shardbatis-config.dtd">
<shardingConfig>
 
 
      <parseList>
         <value>parseId</value>
         <value> <![CDATA[ parseid>2]]></value>
            <value>com.sndj.CountryMapper.query</value>
      </parseList>
 
    <strategy tableName="country"
 strategyClass="com.dld.hll.shardbatis.strategy.impl.GeneralLedgerStrategy"/>
</shardingConfig>


在shard_config.xml中配置预分表的分表策略，如果未配置，将不做逻辑表到真实表的转换。
在shard_config.xml中配置预分表的Mapper的方法，使用类名+方法名，用于说明此方法对应SQL将会被解析，并做分表处理。
在Mapper的方法上面添加标记注解@ShardingTable用于标注此方法将做分表处理。

 
4.转换器

public interface SqlConverter {
   /**
    * 对sql进行修改
    * @param statement 代表SQL语句
    * @param params mybatis执行某个statement时使用的参数
    * @param mapperId mybatis配置的statement id
    * @return
    */
   String convert(Statement statement,Object parameterObject,String mapperId);
}



5.分表策略

public interface ShardStrategy {
    /**
     * 得到实际表名
     *
     * @param originTableName 逻辑表名,一般是没有前缀或者是后缀的表名
     * @param parameterObject          mybatis执行某个statement时使用的参数
     * @param mapperId        mybatis配置的statement id
     * @return 真实表名
     */
    public abstract String getTargetTableName(String originTableName, Object parameterObject, String mapperId);
}



GeneralLedgerStrategy为自实现的分表策略，分片键为accountID和accountYear，即如果使用此分表参数，Mapper方法对应入参必须有accountID和accountYear属性，必须有值。
