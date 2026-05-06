package de.framedev.frameeconomy.mysql;

import de.framedev.frameeconomy.main.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class SQL {

    /**
     * Erstelle einen Table mit einem Table Name und verschiedene Column
     *
     * @param tablename TableName der erstellt wird
     * @param columns   Kolumm die erstellt werden
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public static void createTable(String tablename, String... columns) {
        String builder = join(columns);
        try {
            String sql;
            if (Main.getInstance().isMysql()) {
                sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (" + builder + ",Numbers INT AUTO_INCREMENT KEY,created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
            } else if (Main.getInstance().isH2()) {
                sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (ID IDENTITY PRIMARY KEY," + normalizeColumnsForH2(builder) + ",created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
            } else if (Main.getInstance().isSQL()) {
                sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," + builder + ",created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
            } else {
                return;
            }
            try (PreparedStatement stmt = connection().prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logSqlException("createTable", sqlPreview(tablename), e);
        } finally {
            close();
        }
    }

    /**
     * @param table   the Table in the Database
     * @param data    data to set in the Database
     * @param columns the Columns in the Database to add new Data
     */
    public static void insertData(String table, String data, String... columns) {
        String sql = "INSERT INTO " + table + " (" + join(columns) + ") VALUES (" + data + ")";
        executeUpdate(sql);
    }

    /**
     * @param table    the Selected Table
     * @param selected where you would like to Update data
     * @param data     the new Data
     * @param where    witch row you want do Update
     */
    public static void updateData(String table, String selected, String data, String where) {
        executeUpdate("UPDATE " + table + " SET " + selected + " = " + data + " WHERE " + where);
    }

    /**
     * @param table The Table in the Database
     * @param where where you would like to remove Data
     */
    public static void deleteDataInTable(String table, String where) {
        executeUpdate("DELETE FROM " + table + " WHERE " + where);
    }

    /**
     * @param table the Table in the Database
     * @param where where you want to remove Datas
     * @param and   the same as where
     */
    public static void deleteDataInTable(String table, String where, String and) {
        executeUpdate("DELETE FROM " + table + " WHERE " + where + " AND " + and + ";");
    }

    /**
     * @param table  the Table in the Database
     * @param column where you would like to search
     * @param data   the data of where
     * @return if the Data exists
     */
    public static boolean exists(String table, String column, String data) {
        return exists(table, column, data, null);
    }

    public static boolean exists(String table, String column, String data, String and) {
        String sql = "SELECT 1 FROM " + table + " WHERE " + column + " = ?";
        if (and != null && !and.trim().isEmpty()) {
            sql += " AND " + and;
        }
        sql += " LIMIT 1";

        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, data);
            try (ResultSet res = statement.executeQuery()) {
                return res.next();
            }
        } catch (SQLException e) {
            logSqlException("exists", sql, e);
        } finally {
            close();
        }
        return false;
    }

    /**
     * @param table    the Table in the Database
     * @param selected the Selected Column in the Database
     * @param column   the Column you would like to search
     * @param data     the Data of the where Column
     * @return the Data from the Database
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public static Object get(String table, String selected, String column, String data) {
        String sql = "SELECT " + selected + " FROM " + table + " WHERE " + column + " = ? LIMIT 1";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, data);
            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    return res.getObject(selected);
                }
            }
        } catch (SQLException e) {
            logSqlException("get", sql, e);
        } finally {
            close();
        }
        return null;
    }

    /**
     * @param table the Table you would like to Delete
     */
    public static void deleteTable(String table) {
        executeUpdate("DROP TABLE " + table);
    }

    /**
     * @param table the Table do you want to search
     * @return if Table exists or not
     */
    public static boolean isTableExists(String table) {
        try {
            if (Main.getInstance().isMysql()) {
                try (PreparedStatement statement = connection().prepareStatement("SHOW TABLES LIKE ?")) {
                    statement.setString(1, table);
                    try (ResultSet rs = statement.executeQuery()) {
                        return rs.next();
                    }
                }
            } else if (Main.getInstance().isH2()) {
                String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = CURRENT_SCHEMA() AND UPPER(TABLE_NAME) = UPPER(?)";
                try (PreparedStatement statement = connection().prepareStatement(sql)) {
                    statement.setString(1, table);
                    try (ResultSet rs = statement.executeQuery()) {
                        return rs.next();
                    }
                }
            } else if (Main.getInstance().isSQL()) {
                try (PreparedStatement statement = connection().prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name = ?")) {
                    statement.setString(1, table);
                    try (ResultSet rs = statement.executeQuery()) {
                        return rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            logSqlException("isTableExists", "table=" + table, e);
        } finally {
            close();
        }
        return false;
    }

    private static void executeUpdate(String sql) {
        try (Statement stmt = connection().createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logSqlException("executeUpdate", sql, e);
        } finally {
            close();
        }
    }

    public static Connection connection() throws SQLException {
        Connection connection;
        if (Main.getInstance().isMysql()) {
            connection = MySQL.getConnection();
        } else if (Main.getInstance().isH2()) {
            connection = H2.connect();
        } else if (Main.getInstance().isSQL()) {
            connection = SQLite.connect();
        } else {
            throw new SQLException("No SQL database backend is enabled.");
        }
        if (connection == null) {
            throw new SQLException("Could not open SQL database connection.");
        }
        return connection;
    }

    public static void close() {
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isH2()) {
            H2.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }

    private static String join(String... values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            stringBuilder.append(values[i]);
            if (i < values.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private static String normalizeColumnsForH2(String columns) {
        return columns.replaceAll("(?i)TEXT\\((\\d+)\\)", "VARCHAR($1)")
                .replaceAll("(?i)\\bTEXT\\b", "VARCHAR(8192)");
    }

    private static void logSqlException(String operation, String sql, SQLException exception) {
        String sqlSnippet = sql == null ? "n/a" : sql;
        if (sqlSnippet.length() > 300) {
            sqlSnippet = sqlSnippet.substring(0, 300) + "...";
        }
        logger().log(Level.SEVERE,
                "SQL error during " + operation + " [backend=" + backendName() + ", sql=" + sqlSnippet + "]",
                exception);
    }

    private static Logger logger() {
        Main plugin = Main.getInstance();
        return plugin != null ? plugin.getLogger() : Logger.getLogger(SQL.class.getName());
    }

    private static String backendName() {
        Main plugin = Main.getInstance();
        if (plugin == null) {
            return "unknown";
        }
        if (plugin.isMysql()) {
            return "mysql";
        }
        if (plugin.isH2()) {
            return "h2";
        }
        if (plugin.isSQL()) {
            return "sqlite";
        }
        return "none";
    }

    private static String sqlPreview(String tableName) {
        return "CREATE TABLE" + " on " + tableName;
    }
}
