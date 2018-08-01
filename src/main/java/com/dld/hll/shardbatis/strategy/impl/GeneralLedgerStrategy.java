package com.dld.hll.shardbatis.strategy.impl;

import com.dld.hll.shardbatis.ShardException;
import com.dld.hll.shardbatis.strategy.ShardStrategy;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * // 表名加后两位 _accountID MOD 10 _ accountYear MOD 10
 * @author yuechao 2018/7/31
 */
public class GeneralLedgerStrategy implements ShardStrategy {

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
        if (accountID == null) {
            throw new ShardException("accountID not in parameter");
        }
        if (accountYear == null) {
            throw new ShardException("accountYear not in parameter");

        }
        sb.append("_").append(Integer.valueOf(accountID.toString()) % 10).append(Integer.valueOf(accountYear.toString()) % 10);

        return sb.toString();


    }
}
