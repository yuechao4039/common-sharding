/**
 * 
 */
package com.dld.hll.shardbatis.builder;

import com.dld.hll.shardbatis.ShardException;
import com.dld.hll.shardbatis.strategy.ShardStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




public class ShardConfigHolder {
	private static final ShardConfigHolder instance = new ShardConfigHolder();

	public static ShardConfigHolder getInstance() {
		return instance;
	}

    /**
     * key:表名 value:分表策略
     */
	private Map<String, ShardStrategy> strategyRegister = new HashMap<String, ShardStrategy>();

	private Set<String> ignoreSet = new HashSet<>();
	private Set<String> parseSet = new HashSet<>();

	private Set<String> totalSet = new HashSet<>();

	private ShardConfigHolder() {
	}

	/**
	 * 注册分表策略
	 * 
	 * @param table
	 * @param strategy
	 */
	public void register(String table, ShardStrategy strategy) {
		this.strategyRegister.put(table.toLowerCase(), strategy);
	}

	/**
	 * 查找对应表的分表策略
	 * 
	 * @param table
	 * @return
	 */
	public ShardStrategy getStrategy(String table) {
        ShardStrategy strategy = strategyRegister.get(table.toLowerCase());
        return  strategy;
	}

	/**
	 * 增加ignore id配置
	 * 
	 * @param id
	 */
	public synchronized void addIgnoreId(String id) {

		if (totalSet.contains(id)) {
		    throw new ShardException(id + " has already existed ");
        }
		ignoreSet.add(id);
		totalSet.add(id);
	}

	/**
	 * 增加parse id配置
	 * @param id
	 */
	public synchronized void addParseId(String id) {
        if (totalSet.contains(id)) {
            throw new ShardException(id + " has already existed ");
        }
		parseSet.add(id);
        totalSet.add(id);
	}
	/**
	 * 判断参数ID是否在配置的parse id范围内
	 * @param id
	 * @return
	 */
	public boolean isParseId(String id) {
		return parseSet.contains(id);
	}

	/**
	 * 判断参数ID是否在配置的ignore id范围内
	 * @param id
	 * @return
	 */
	public boolean isIgnoreId(String id) {
		return ignoreSet.contains(id);
	}
}
