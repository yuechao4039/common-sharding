/**
 * 
 */
package com.dld.hll.shardbatis.strategy.impl;

import com.dld.hll.shardbatis.strategy.ShardStrategy;


public class TestShardStrategyImpl implements ShardStrategy {

	/* (non-Javadoc)
	 * @see com.google.code.shardbatis.strategy.ShardStrategy#getTargetTableName(java.lang.String, java.lang.Object, java.lang.String)
	 */
	public String getTargetTableName(String baseTableName, Object params,
			String mapperId) {
		return baseTableName+"_xx";
	}

}
