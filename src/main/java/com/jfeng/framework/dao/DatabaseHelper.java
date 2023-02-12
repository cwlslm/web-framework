package com.jfeng.framework.dao;

import com.jfeng.framework.core.ConfigHelper;
import com.jfeng.framework.util.CastUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库操作助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final class ConnectionPojo {

        private int id;

        private Connection connection;

        ConnectionPojo (int id, Connection connection) {
            this.id = id;
            this.connection = connection;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }
    }

    private static final ThreadLocal<List<ConnectionPojo>> CONNECTION_HOLDER;

    private static final QueryRunner QUERY_RUNNER;

    private static final BasicDataSource DATA_SOURCE;

    static {
        CONNECTION_HOLDER = new ThreadLocal<>();

        QUERY_RUNNER = new QueryRunner();

        String driver = ConfigHelper.getJdbcDriver();
        String url = ConfigHelper.getJdbcUrl();
        String username = ConfigHelper.getJdbcUsername();
        String password = ConfigHelper.getJdbcPassword();
        int initSize = ConfigHelper.getJdbcInitSize();
        int maxTotal = ConfigHelper.getJdbcMaxTotal();
        int minIdle = ConfigHelper.getJdbcMinIdle();
        int maxIdle = ConfigHelper.getJdbcMaxIdle();
        int maxWait = ConfigHelper.getJdbcMaxWait();
        String validationQuery = ConfigHelper.getJdbcValidationQuery();
        Boolean testOnBorrow = ConfigHelper.getJdbcTestOnBorrow();
        Boolean testOnReturn = ConfigHelper.getJdbcTestOnReturn();
        Boolean testWhileIdle = ConfigHelper.getJdbcTestWhileIdle();
        int minEvictableIdleTimeMillis = ConfigHelper.getJdbcMinEvictableIdleTimeMillis();
        int timeBetweenEvictionRunsMillis = ConfigHelper.getJdbcTimeBetweenEvictionRunsMillis();
        List<String> connectInitSqls = ConfigHelper.getJdbcConnectInitSqls();

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
        if (initSize != 0) {
            DATA_SOURCE.setInitialSize(initSize);
        }
        if (maxTotal != 0) {
            DATA_SOURCE.setMaxTotal(maxTotal);
        }
        if (minIdle != 0) {
            DATA_SOURCE.setMinIdle(minIdle);
        }
        if (maxIdle != 0) {
            DATA_SOURCE.setMaxIdle(maxIdle);
        }
        if (maxWait != 0) {
            DATA_SOURCE.setMaxWaitMillis(maxWait);
        }
        if (validationQuery != null) {
            DATA_SOURCE.setValidationQuery(validationQuery);
        }
        DATA_SOURCE.setTestOnBorrow(testOnBorrow);
        DATA_SOURCE.setTestOnReturn(testOnReturn);
        DATA_SOURCE.setTestWhileIdle(testWhileIdle);
        if (minEvictableIdleTimeMillis != 0) {
            DATA_SOURCE.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }
        if (timeBetweenEvictionRunsMillis != 0) {
            DATA_SOURCE.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if (connectInitSqls != null) {
            DATA_SOURCE.setConnectionInitSqls(connectInitSqls);
        }
    }

    /**
     * 获取数据库连接（功能扩展版） 指定ID
     */
    public static ConnectionPojo getConnectionEx(int id) {
        List<ConnectionPojo> connPojoList = CONNECTION_HOLDER.get();
        if (connPojoList == null || connPojoList.size() == 0) {
            try {
                connPojoList = new ArrayList<>();
                Connection conn = DATA_SOURCE.getConnection();
                connPojoList.add(new ConnectionPojo(0, conn));
            } catch (SQLException e) {
                LOGGER.error("getConnectionEx SQLException异常", e);
                throw new RuntimeException(e);
            }
            finally {
                CONNECTION_HOLDER.set(connPojoList);
            }
        }

        if (id >= 0) {
            int findIndex = 0;
            for (; findIndex < connPojoList.size(); findIndex++) {
                if (connPojoList.get(findIndex).getId() == id) {
                    break;
                }
            }

            if (findIndex < connPojoList.size()) {
                return connPojoList.get(findIndex);
            }
            else {
                return null;
            }
        }

        return connPojoList.get(connPojoList.size() - 1);
    }

    /**
     * 获取数据库连接（功能扩展版）
     */
    public static ConnectionPojo getConnectionEx() {
        return getConnectionEx(-1);
    }

    /**
     * 获取数据库连接（指定ID）
     */
    public static Connection getConnection(int id) {
        ConnectionPojo connPojo = getConnectionEx(id);
        if (connPojo != null) {
            return connPojo.getConnection();
        }
        return null;
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        return getConnectionEx().getConnection();
    }

    /**
     * 获取新的数据库连接（功能扩展版）
     */
    public static ConnectionPojo getNewConnectionEx() {
        List<ConnectionPojo> connPojoList = CONNECTION_HOLDER.get();
        try {
            Connection conn = DATA_SOURCE.getConnection();
            if (connPojoList == null) {
                connPojoList = new ArrayList<>();
            }

            if (connPojoList.size() == 0) {
                connPojoList.add(new ConnectionPojo(0, conn));
            }
            else {
                connPojoList.add(new ConnectionPojo(connPojoList.get(connPojoList.size() - 1).getId() + 1, conn));
            }
        } catch (SQLException e) {
            LOGGER.error("getNewConnectionEx SQLException异常", e);
            throw new RuntimeException(e);
        } finally {
            CONNECTION_HOLDER.set(connPojoList);
        }

        return connPojoList.get(connPojoList.size() - 1);
    }

    /**
     * 获取新的数据库连接
     */
    public static Connection getNewConnection() {
        return getNewConnectionEx().getConnection();
    }

    /**
     * 释放一个连接（指定ID）
     */
    public static void closeConnection(int id) {
        List<ConnectionPojo> connPojoList = CONNECTION_HOLDER.get();
        if (connPojoList != null && connPojoList.size() > 0) {
            int findIndex = 0;
            try {
                if (id >= 0) {
                    for (; findIndex < connPojoList.size(); findIndex++) {
                        if (connPojoList.get(findIndex).getId() == id) {
                            break;
                        }
                    }
                }
                else {
                    findIndex = connPojoList.size() - 1;
                }

                if (findIndex < connPojoList.size()) {
                    Connection conn = connPojoList.get(findIndex).getConnection();
                    try {
                        conn.setAutoCommit(true);
                    }
                    catch (SQLException e) {
                        LOGGER.error("closeConnection setAutoCommit(true) SQLException异常", e);
                    }
                    conn.close();
                }
                else {
                    findIndex = -1;
                }
            } catch (SQLException e) {
                LOGGER.error("closeConnection SQLException异常", e);
            } finally {
                if (findIndex >= 0) {
                    connPojoList.remove(findIndex);
                }
                CONNECTION_HOLDER.set(connPojoList);
            }
        }
    }

    /**
     * 释放一个连接（列表最后一个）
     */
    public static void closeConnection() {
        closeConnection(-1);
    }

    /**
     * 释放所有连接
     */
    public static void closeAllConnection() {
        List<ConnectionPojo> connPojoList = CONNECTION_HOLDER.get();
        if (connPojoList != null && connPojoList.size() > 0) {
            for (int count = connPojoList.size(); count > 0; count--) {
                closeConnection();
            }
        }
    }

    /**
     * 执行查询语句
     * 返回第一条数据
     * 返回 Map<String, Object>
     */
    public static Map<String, Object> queryMap(String sql, Object... params) {
        Map<String, Object> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("queryMap SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回 List<Map<String, Object>>
     */
    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        List<Map<String,Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("queryMapList SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回第一条数据
     * 返回 T
     */
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("queryEntity SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回 List<T>
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("queryEntityList SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回第一条数据，并将其封装到一个数组中，一列值对应一个数组元素
     * 返回 Object[]
     */
    public static Object[] queryArray(String sql, Object... params) {
        Object[] result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new ArrayHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("queryArray SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回 List<Object[]>
     */
    public static List<Object[]> queryArrayList(String sql, Object... params) {
        List<Object[]> result = new ArrayList<>();
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new ArrayListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("queryArrayList SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 用于获取所有结果集，将每行结果集转换为 Javabean 作为 value，并指定某列为 key，封装到 HashMap 中
     * 相当于对每行数据的做 BeanHandler 一样的处理后，再指定某列为 Key 封装到 HashMap 中
     * 返回 Map<K, V>
     */
    public static <K, V> Map<K, V> queryEntityMapByKey(String key, Class<V> entityClass, String sql, Object... params) {
        Map<K, V> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new BeanMapHandler<K, V>(entityClass, key), params);
        } catch (SQLException e) {
            LOGGER.error("queryEntityMapByKey SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 用于获取所有结果集，将每行结果集转换为 Map<String, Object>
     * 相当于对每行数据的做 MapHandler 一样的处理后，再指定某列为 Key 封装到 HashMap 中
     * 返回 Map<T, Map<String, Object>>
     */
    public static <T> Map<T, Map<String, Object>> queryColumnMapByKey(String key, String sql, Object... params) {
        Map<T, Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new KeyedHandler<T>(key), params);
        } catch (SQLException e) {
            LOGGER.error("queryColumnMapByKey SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回第一行第一列
     * 返回 T
     */
    public static <T> T queryColumn(String sql, Object... params) {
        T result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new ScalarHandler<T>(), params);
        } catch (SQLException e) {
            LOGGER.error("queryColumn SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回第一行的某一列
     * 返回 T
     */
    public static <T> T queryColumn(String columnName, String sql, Object... params) {
        T result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new ScalarHandler<T>(columnName), params);
        } catch (SQLException e) {
            LOGGER.error("queryColumn SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回结果集数据数量
     * 返回 long
     */
    public static long queryCount(String sql, Object... params) {
        long result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, "sql", new ScalarHandler<Long>("count(*)"), params);
        } catch (SQLException e) {
            LOGGER.error("queryCount SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回查询结果的某一列
     * 返回 List<T>
     */
    public static <T> List<T> queryColumnList(String columnName, String sql, Object... params) {
        List<T> result = new ArrayList<>();
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new ColumnListHandler<>(columnName), params);
        } catch (SQLException e) {
            LOGGER.error("queryColumnList SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行查询语句
     * 返回查询结果的第一列
     * 返回 List<T>
     */
    public static <T> List<T> queryColumnList(String sql, Object... params) {
        List<T> result = new ArrayList<>();
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new ColumnListHandler<>(), params);
        } catch (SQLException e) {
            LOGGER.error("queryColumnList SQLException异常", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行更新语句（包括update、insert、delete）
     * 返回更新的条数
     */
    public static int update(String sql, Object... params) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("update SQLException异常", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 执行插入语句
     * 返回主键
     */
    public static long insert(String sql, Object... params) {
        long pk;
        try {
            Connection conn = getConnection();
            Object result = QUERY_RUNNER.insert(conn, sql, new ScalarHandler<Long>(), params);
            pk = CastUtil.castLong(result);
        } catch (SQLException e) {
            LOGGER.error("insert SQLException异常", e);
            throw new RuntimeException(e);
        }
        return pk;
    }

    /**
     * 执行 SQL 文件
     */
    public static void executeSqlFile(String filePath) throws FileNotFoundException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            throw new FileNotFoundException(filePath + "file is not found");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String sql;
            while((sql =reader.readLine()) != null) {
                update(sql);
            }
        } catch (Exception e) {
            LOGGER.error("executeSqlFile 异常", e);
        }
    }

    /**
     * 开启事务
     */
    public static int beginTransaction() {
        ConnectionPojo connPojo = getConnectionEx();
        if (connPojo != null && connPojo.getConnection() != null) {
            try {
                if (!connPojo.getConnection().getAutoCommit()) {
                    connPojo = getNewConnectionEx();
                }
                connPojo.getConnection().setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.error("beginTransaction SQLException异常", e);
                throw new RuntimeException(e);
            }
        }

        if (connPojo == null) {
            return 0;
        }

        return connPojo.getId();
    }

    /**
     * 提交事务（指定ID）
     */
    public static void commitTransaction(int id) {
        Connection conn = getConnection(id);
        if (conn != null) {
            try {
                conn.commit();
            }
            catch (SQLException e) {
                LOGGER.error("commitTransaction SQLException异常", e);
                throw new RuntimeException(e);
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                }
                catch (SQLException e) {
                    LOGGER.error("commit transaction setAutoCommit(true) SQLException异常", e);
                }
                // 如果是嵌套事务，需要移除本层事务的连接，切换回外层事务的连接
                if (CONNECTION_HOLDER.get().size() > 1) {
                    closeConnection(id);
                }
            }
        }
    }

    /**
     * 提交事务（列表最后一个）
     */
    public static void commitTransaction() {
        commitTransaction(-1);
    }

    /**
     * 回滚事务（指定ID）
     */
    public static void rollbackTransaction(int id) {
        Connection conn = getConnection(id);
        if (conn != null) {
            try {
                conn.rollback();
            }
            catch (SQLException e) {
                LOGGER.error("rollbackTransaction SQLException异常", e);
                throw new RuntimeException(e);
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                }
                catch (SQLException e) {
                    LOGGER.error("rollbackTransaction setAutoCommit(true) SQLException异常", e);
                }
                // 如果是嵌套事务，需要移除本层事务的连接，切换回外层事务的连接
                if (CONNECTION_HOLDER.get().size() > 1) {
                    closeConnection(id);
                }
            }
        }
    }

    /**
     * 回滚事务（列表最后一个）
     */
    public static void rollbackTransaction() {
        rollbackTransaction(-1);
    }
}
