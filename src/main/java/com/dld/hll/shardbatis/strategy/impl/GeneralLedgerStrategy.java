package com.dld.hll.shardbatis.strategy.impl;

import com.dld.hll.shardbatis.strategy.ShardStrategy;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * @author yuechao 2018/7/31
 */
public class GeneralLedgerStrategy implements ShardStrategy {

    private static final String SHARD_FIELD_ACCOUNTID = "accountID";

    // 表名加后两位 _accountID MOD 10 _ accountYear MOD 10

    private static final String SHARD_FIELD_ACCOUNTYEAR = "accountYear";

    public String getTargetTableName(String baseTableName, Object parameterObject, String mapperId) {
        return getTableName(baseTableName, parameterObject);
    }

    private String getTableName(String originTableName, Object parameterObject) {

        MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
        StringBuilder sb = new StringBuilder(originTableName);

        String fieldValue = String.valueOf(metaObject.getValue(SHARD_FIELD_ACCOUNTID));

        sb.append("_").append(fieldValue);

        fieldValue = String.valueOf(metaObject.getValue(SHARD_FIELD_ACCOUNTYEAR));

        sb.append("_").append(fieldValue);
        return sb.toString();


    }
}
