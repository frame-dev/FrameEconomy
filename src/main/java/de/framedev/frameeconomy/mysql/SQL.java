package de.framedev.frameeconomy.mysql;

import de.framedev.frameeconomy.main.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQL {

    /**
     * Erstelle einen Table mit einem Table Name und verschiedene Column
     *
     * @param tablename TableName der erstellt wird
     * @param columns   Kolumm die erstellt werden
     */
    public static void createTable(String tablename, String... columns) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            stringBuilder.append(columns[i]);
            if (i < columns.length - 1) {
                stringBuilder.append(",");
            }
        }
        String builder = stringBuilder.toString();
        try {
            if (Main.getInstance().isMysql()) {
                String sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (" + builder + ",Numbers INT AUTO_INCREMENT KEY,created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
                PreparedStatement stmt = MySQL.getConnection().prepareStatement(sql);
                stmt.executeUpdate();
            } else if (Main.getInstance().isSQL()) {
                String sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," + builder + ",created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
                PreparedStatement stmt = SQLite.connect().prepareStatement(sql);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }

    public static void insertData(String table, String data, String... columns) {
        StringBuilder newStringBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            newStringBuilder.append(columns[i]);
            if (i < columns.length - 1) {
                newStringBuilder.append(",");
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO " + table);
        stringBuilder.append(" (").append(newStringBuilder.toString()).append(")").append(" VALUES ").append("(").append(data).append(")");
        String builder2 = stringBuilder.toString();
        try {
            if (Main.getInstance().isMysql()) {
                Statement stmt = MySQL.getConnection().createStatement();
                stmt.executeUpdate(builder2);
            } else if (Main.getInstance().isSQL()) {
                Statement stmt = SQLite.connect().createStatement();
                stmt.executeUpdate(builder2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }

    public static void updateData(String table, String selected, String data, String where) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE " + table + " SET ").append(selected + " = " + data).append(" WHERE " + where);
        String sql = stringBuilder.toString();
        try {
            if (Main.getInstance().isMysql()) {
                Statement stmt = MySQL.getConnection().createStatement();
                stmt.executeUpdate(sql);
            } else if (Main.getInstance().isSQL()) {
                Statement stmt = SQLite.connect().createStatement();
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }


    public static void deleteDataInTable(String table, String where) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + table)
                .append(" WHERE " + where);
        String sql = sb.toString();
        try {
            if (Main.getInstance().isMysql()) {
                Statement stmt = MySQL.getConnection().createStatement();
                stmt.executeUpdate(sql);
            } else if (Main.getInstance().isSQL()) {
                Statement stmt = SQLite.connect().createStatement();
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }

    public static void deleteDataInTable(String table, String where, String and) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + table)
                .append(" WHERE " + where)
                .append(" AND " + and + ";");
        String sql = sb.toString();
        try {
            if (Main.getInstance().isMysql()) {
                Statement stmt = MySQL.getConnection().createStatement();
                stmt.executeUpdate(sql);
            } else if (Main.getInstance().isSQL()) {
                Statement stmt = SQLite.connect().createStatement();
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }

    public static boolean exists(String table, String column, String data) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ")
                .append(table)
                .append(" WHERE ")
                .append(column)
                .append(" = '" + data + "';");

        try {
            if (Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                String sql = stringBuilder.toString();
                ResultSet res = statement.executeQuery(sql);
                if (res.next()) {
                    if (res.getString(column) == null) {
                        return false;
                    } else {
                        res.close();
                        statement.close();
                        return true;
                    }
                }
            } else if (Main.getInstance().isSQL()) {
                Statement statement = SQLite.connect().createStatement();
                String sql = stringBuilder.toString();
                ResultSet res = statement.executeQuery(sql);
                if (res.next()) {
                    if (res.getString(column) == null) {
                        return false;
                    } else {
                        res.close();
                        statement.close();
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
        return false;
    }

    public static boolean exists(String table, String column, String data, String and) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ")
                .append(table)
                .append(" WHERE ")
                .append(column)
                .append(" = '" + data + "' AND " + and + ";");

        try {
            if (Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                String sql = stringBuilder.toString();
                ResultSet res = statement.executeQuery(sql);
                if (res.next()) {
                    if (res.getString(column) == null) {
                        return false;
                    }
                    return true;
                }
            } else if (Main.getInstance().isSQL()) {
                Statement statement = SQLite.connect().createStatement();
                String sql = stringBuilder.toString();
                ResultSet res = statement.executeQuery(sql);
                if (res.next()) {
                    if (res.getString(column) == null) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
        return false;
    }


    public static Object get(String table, String selected, String column, String data) {
        Object o = null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ")
                .append(table)
                .append(" WHERE " + column + " = '")
                .append(data)
                .append("'");
        String sql = stringBuilder.toString();
        try {
            if (Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet res = statement.executeQuery(sql);
                if (res.next()) {
                    o = res.getObject(selected);
                    if (o != null) {
                        return o;
                    }
                    return o;
                }
            } else if (Main.getInstance().isSQL()) {
                Statement statement = SQLite.connect().createStatement();
                ResultSet res = statement.executeQuery(sql);
                if (res.next()) {
                    o = res.getObject(selected);
                    if (o != null) {
                        return o;
                    }
                    return o;
                }
            }
            return o;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
        return o;
    }

    public static void deleteTable(String table) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DROP TABLE " + table);
        String sql = stringBuilder.toString();

        try {
            if (Main.getInstance().isMysql()) {
                Statement stmt = MySQL.getConnection().createStatement();
                stmt.executeUpdate(sql);
            } else if(Main.getInstance().isSQL()) {
                Statement stmt = SQLite.connect().createStatement();
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
    }

    public static boolean isTableExists(String table) {
        try {
            if(Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet rs = statement.executeQuery("SHOW TABLES LIKE '" + table + "'");
                if (rs.next()) {
                    return true;
                }
            } else if(Main.getInstance().isSQL()) {
                Statement statement = SQLite.connect().createStatement();
                ResultSet rs = statement.executeQuery("SELECT \n" +
                        "    name\n" +
                        "FROM \n" +
                        "    sqlite_master \n" +
                        "WHERE \n" +
                        "    type ='table' AND \n" +
                        "    name NOT LIKE 'sqlite_%';");
                while (rs.next()) {
                    if(rs.getString("name").equalsIgnoreCase(table))
                        return true;
                }
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL()) {
                SQLite.close();
            }
        }
        if (Main.getInstance().isMysql()) {
            MySQL.close();
        } else if (Main.getInstance().isSQL()) {
            SQLite.close();
        }
        return false;
    }
}


