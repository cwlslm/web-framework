package com.jfeng.framework.orm;

import com.jfeng.framework.core.ConfigConstant;
import com.jfeng.framework.core.ConfigHelper;
import com.jfeng.framework.orm.fault.SqlGenerateError;
import com.jfeng.framework.util.CollectionUtil;
import com.jfeng.framework.util.MapUtil;
import com.jfeng.framework.util.PropsUtil;
import com.jfeng.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * 封装 SQL 语句相关操作
 *
 * @author jfeng
 * @since 1.0.0
 */
public class SqlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlHelper.class);

    /**
     * SQL 属性文件对象
     */
    private static final Properties sqlProps = PropsUtil.loadProps(ConfigConstant.SQL_PROPS);

    /**
     * 从 SQL 属性文件中获取相应的 SQL 语句
     */
    public static String getSql(String key) {
        String sql;
        if (sqlProps.containsKey(key)) {
            sql = sqlProps.getProperty(key);
        } else {
            throw new RuntimeException("无法在 " + ConfigConstant.SQL_PROPS + " 文件中获取属性：" + key);
        }
        return sql;
    }

    /**
     * 生成 select 语句
     */
    public static String generateSelectSql(Class<?> entityClass, String condition, String sort) {
        StringBuilder sql = new StringBuilder("select * from ").append(getTable(entityClass));
        sql.append(generateWhere(condition));
        sql.append(generateOrder(sort));
        return sql.toString();
    }

    /**
     * 生成 insert 语句
     */
    public static String generateInsertSql(Class<?> entityClass, Collection<String> fieldNames) {
        if (CollectionUtil.isEmpty(fieldNames)) {
            LOGGER.error("generateInsertSql 抛出异常，fieldNames不能为空");
            throw new SqlGenerateError("can not insert entity: fieldNames is empty");
        }

        StringBuilder sql = new StringBuilder("insert into ").append(getTable(entityClass));
        int i = 0;
        StringBuilder columns = new StringBuilder(" ");
        StringBuilder values = new StringBuilder(" values ");
        for (String fieldName : fieldNames) {
            String columnName = EntityHelper.getColumnName(entityClass, fieldName);
            if (i == 0) {
                columns.append("(").append(columnName);
                values.append("(?");
            } else {
                columns.append(", ").append(columnName);
                values.append(", ?");
            }
            if (i == fieldNames.size() - 1) {
                columns.append(")");
                values.append(")");
            }
            i++;
        }
        sql.append(columns).append(values);
        return sql.toString();
    }

    /**
     * 生成 delete 语句
     */
    public static String generateDeleteSql(Class<?> entityClass, String condition) {
        if (StringUtil.isEmpty(condition)) {
            LOGGER.error("generateDeleteSql 抛出异常，condition不能为空");
            throw new SqlGenerateError("can not delete entity: condition is empty");
        }

        StringBuilder sql = new StringBuilder("delete from ").append(getTable(entityClass));
        sql.append(generateWhere(condition));
        return sql.toString();
    }

    /**
     * 生成 update 语句
     */
    public static String generateUpdateSql(Class<?> entityClass, Map<String, Object> fieldMap, String condition) {
        if (StringUtil.isEmpty(condition)) {
            LOGGER.error("generateUpdateSql 抛出异常，condition不能为空");
            throw new SqlGenerateError("can not update entity: condition is empty");
        }
        if (MapUtil.isEmpty(fieldMap)) {
            LOGGER.error("generateUpdateSql 抛出异常，fieldMap不能为空");
            throw new SqlGenerateError("can not update entity: fieldMap is empty");
        }

        StringBuilder sql = new StringBuilder("update ").append(getTable(entityClass));
        sql.append(" set ");
        int i = 0;
        for (Map.Entry<String, Object> fieldEntry : fieldMap.entrySet()) {
            String fieldName = fieldEntry.getKey();
            String columnName = EntityHelper.getColumnName(entityClass, fieldName);
            if (i == 0) {
                sql.append(columnName).append(" = ?");
            } else {
                sql.append(", ").append(columnName).append(" = ?");
            }
            i++;
        }
        sql.append(generateWhere(condition));
        return sql.toString();
    }

    /**
     * 生成 select count(*) 语句
     */
    public static String generateSelectSqlForCount(Class<?> entityClass, String condition) {
        StringBuilder sql = new StringBuilder("select count(*) from ").append(getTable(entityClass));
        sql.append(generateWhere(condition));
        return sql.toString();
    }

    /**
     * 生成 select 分页语句（数据库类型为：mysql、oracle、mssql）
     */
    public static String generateSelectSqlForPager(int pageNumber, int pageSize, Class<?> entityClass, String condition, String sort) {
        StringBuilder sql = new StringBuilder();
        String table = getTable(entityClass);
        String where = generateWhere(condition);
        String order = generateOrder(sort);
        String dbType = ConfigHelper.getJdbcType();
        if (dbType.equalsIgnoreCase("mysql")) {
            int pageStart = (pageNumber - 1) * pageSize;
            appendSqlForMySql(sql, table, where, order, pageStart, pageSize);
        } else if (dbType.equalsIgnoreCase("oracle")) {
            int pageStart = (pageNumber - 1) * pageSize + 1;
            int pageEnd = pageStart + pageSize;
            appendSqlForOracle(sql, table, where, order, pageStart, pageEnd);
        } else if (dbType.equalsIgnoreCase("mssql")) {
            int pageStart = (pageNumber - 1) * pageSize;
            appendSqlForMsSql(sql, table, where, order, pageStart, pageSize);
        }
        return sql.toString();
    }

    private static String getTable(Class<?> entityClass) {
        return EntityHelper.getTableName(entityClass);
    }

    private static String generateWhere(String condition) {
        String where = "";
        if (StringUtil.isNotEmpty(condition)) {
            where += " where " + condition;
        }
        return where;
    }

    private static String generateOrder(String sort) {
        String order = "";
        if (StringUtil.isNotEmpty(sort)) {
            order += " order by " + sort;
        }
        return order;
    }

    private static void appendSqlForMySql(StringBuilder sql, String table, String where, String order, int pageStart, int pageEnd) {
        /*
            select * from 表名 where 条件 order by 排序 limit 开始位置, 结束位置
         */
        sql.append("select * from ").append(table);
        sql.append(where);
        sql.append(order);
        sql.append(" limit ").append(pageStart).append(", ").append(pageEnd);
    }

    private static void appendSqlForOracle(StringBuilder sql, String table, String where, String order, int pageStart, int pageEnd) {
        /*
            select a.* from (
                select rownum rn, t.* from 表名 t where 条件 order by 排序
            ) a
            where a.rn >= 开始位置 and a.rn < 结束位置
        */
        sql.append("select a.* from (select rownum rn, t.* from ").append(table).append(" t");
        sql.append(where);
        sql.append(order);
        sql.append(") a where a.rn >= ").append(pageStart).append(" and a.rn < ").append(pageEnd);
    }

    private static void appendSqlForMsSql(StringBuilder sql, String table, String where, String order, int pageStart, int pageEnd) {
        /*
            select top 结束位置 * from 表名 where 条件 and id not in (
                select top 开始位置 id from 表名 where 条件 order by 排序
            ) order by 排序
        */
        sql.append("select top ").append(pageEnd).append(" * from ").append(table);
        if (StringUtil.isNotEmpty(where)) {
            sql.append(where).append(" and ");
        } else {
            sql.append(" where ");
        }
        sql.append("id not in (select top ").append(pageStart).append(" id from ").append(table);
        sql.append(where);
        sql.append(order);
        sql.append(") ").append(order);
    }
}
