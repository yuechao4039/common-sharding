package com.dld.hll.shardbatis.strategy;

/**
 * 分表策略接口
 */
public interface ShardStrategy {
    /**
     * 得到实际表名
     *
     * @param originTableName 逻辑表名,一般是没有前缀或者是后缀的表名
     * @param params          mybatis执行某个statement时使用的参数
     * @param mapperId        mybatis配置的statement id
     * @return 物理表名
     */
    public abstract String getTargetTableName(String originTableName, Object params, String mapperId);
}
