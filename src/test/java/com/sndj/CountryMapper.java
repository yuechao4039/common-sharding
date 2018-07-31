package com.sndj;

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
    public List<Country> query(Country country);



    @Select(value = {"SELECT\n" +
            "ABS(-1), 1\n" +
            "FROM\n" +
            "  (SELECT\n" +
            "    a.accountID,\n" +
            "    a.subjectCode,\n" +
            "    a.accountYear,\n" +
            "    a.periodID,\n" +
            "    a.debitAmount,\n" +
            "    a.creditAmount,\n" +
            "    a.currentBalance,\n" +
            "    a.currentBalanceDirection,\n" +
            "    a.action\n" +
            "  FROM\n" +
            "    tbl_account_general_ledger a,\n" +
            "    tbl_account_subject b\n" +
            "  WHERE a.action < 2\n" +
            "    AND b.action < 2\n" +
            "    AND a.subjectCode = b.subjectCode\n" +
            "    AND a.accountID = b.accountID\n" +
            "    AND a.accountYear = 1\n" +
            "    AND CONCAT(a.subjectCode, a.extAccountKey) IN\n" +
            "    (SELECT\n" +
            "      CONCAT(c.subjectCode, c.extAccountKey)\n" +
            "    FROM\n" +
            "      tbl_account_general_ledger c\n" +
            "    WHERE c.action < 2\n" +
            "      AND c.accountYear = '1' AND (\n" +
            "        c.debitAmount != 0\n" +
            "        OR c.creditAmount != 0\n" +
            "      )\n" +
            "      AND c.periodID >= 1\n" +
            "      AND c.periodID <= 1)) t1,\n" +
            "  tbl_account_subject t2\n" +
            "WHERE t1.action < 2\n" +
            "  AND t2.action < 2\n" +
            "  AND t1.subjectCode LIKE CONCAT(t2.subjectCode, '%')\n" +
            "  AND t1.accountID = t2.accountID\n" +
            "  AND t1.accountID = 1\n" +
            "GROUP BY t2.subjectCode"})
//    @ShardingTable(tableName = "country", fieldNames = {"accountID"})
    public List<Country> queryMultiTables(Country country);

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
