package com.sndj;

import com.dld.hll.shardbatis.anno.ShardingTable;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import java.util.List;

/**
 * @author yuechao 2018/7/23
 */

//@CacheNamespace(implementation = PerpetualCache.class, eviction = LruCache.class,
//        flushInterval = 0, size = 1024,
//        readWrite = true, blocking = false,
//        properties = {@Property(name = "key", value = "val")})
public interface CountryMapper {


//    @MapKey(value = "xx")
//    @Results(id = "country", value = {@Result(column = "countrycode", property = "countrycode")})
//    @ResultMap(value = {"country"})
    @Lang(value = XMLLanguageDriver.class)
    @Options( useCache = true,  flushCache = Options.FlushCachePolicy.TRUE,
            resultSetType = ResultSetType.FORWARD_ONLY,
            statementType = StatementType.PREPARED,
            fetchSize = 100, timeout = -1

//            ,useGeneratedKeys = false, keyProperty = "id", keyColumn = ""

//            ,resultSets = "country"
    )
    @Select(value = {"select id, countrycode, countryname, accountID from country where id = #{id, javaType=INTEGER, jdbcType=INTEGER, mode=IN, numericScale=4}"})
    //resultMap  typeHandler  jdbcTypeName
//    @ShardingTable(tableName = "country", fieldNames = {"accountID"})
    @ShardingTable
    public List<Country> query(Country country);



    @Select(value = {"select * from country t1, c_detail t2 where t1.id = t2.id"})
    @ShardingTable
//    @ShardingTable(tableName = "country", fieldNames = {"accountID"})
    public List<Country> queryMultiTables(Country country);

    @ShardingTable
    @Update(value = {"update country set countrycode = #{countrycode}, countryname = #{countryname} where id = #{id} "})
//    @ShardingTable(tableName = "country", fieldNames = {"accountID"})
    public int update(Country country);





//    String[] statement();
//    String keyProperty();
//    String keyColumn() default "";
//    boolean before();
//    Class<?> resultType();
//    StatementType statementType() default StatementType.PREPARED;

//    @SelectKey(statement =  {}, keyProperty = "", keyColumn = "",
//            before = false, resultType = Class.class, statementType = StatementType.PREPARED)
//    @Update(value = "")
//    public int update();

}
