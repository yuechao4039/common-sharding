package com.dld.hll.shardbatis.strategy.impl;

import com.dld.hll.shardbatis.ShardException;
import com.dld.hll.shardbatis.plugin.ShardPlugin;
import com.dld.hll.shardbatis.strategy.ShardStrategy;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * // 表名加后两位 _accountID MOD 10 _ accountYear MOD 10
 * @author yuechao 2018/7/31
 */

public class GeneralLedgerStrategy implements ShardStrategy {

    private static final Log log = LogFactory.getLog(GeneralLedgerStrategy.class);

    private static final String SHARD_FIELD_ACCOUNTID = "accountID";



    private static final String SHARD_FIELD_ACCOUNTYEAR = "accountYear";

    public String getTargetTableName(String baseTableName, Object parameterObject, String mapperId) {
        return getTableName(baseTableName, parameterObject);
    }

    private String getTableName(String originTableName, Object parameterObject) {

        MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
        StringBuilder sb = new StringBuilder(originTableName);

        Object accountID = metaObject.getValue(SHARD_FIELD_ACCOUNTID);
        Object accountYear = metaObject.getValue(SHARD_FIELD_ACCOUNTYEAR);
        if (log.isDebugEnabled()) {
            log.debug("分片键：" + SHARD_FIELD_ACCOUNTID + ":" + accountID + ", " + SHARD_FIELD_ACCOUNTYEAR + ":" + accountYear);
        }
        if (accountID == null) {
            throw new ShardException("accountID not in parameter");
        }
        if (accountYear == null) {
            throw new ShardException("accountYear not in parameter");
        }
        String realName = "" + Integer.valueOf(accountID.toString()) % 10 + Integer.valueOf(accountYear.toString()) % 10;
        if (log.isDebugEnabled()) {
            log.debug("真实表：" + realName);
        }
        sb.append("_").append(realName);

        return sb.toString();


    }
}
