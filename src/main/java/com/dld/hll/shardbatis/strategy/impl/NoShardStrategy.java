/**
 *
 */
package com.dld.hll.shardbatis.strategy.impl;

import com.dld.hll.shardbatis.strategy.ShardStrategy;

/**
 * 不进行分表的策略，供测试用
 */
public class NoShardStrategy implements ShardStrategy {


    public String getTargetTableName(String baseTableName, Object parameterObject,
                                     String mapperId) {
       return baseTableName;
    }



}
