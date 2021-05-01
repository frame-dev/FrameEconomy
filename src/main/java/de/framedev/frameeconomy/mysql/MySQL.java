package de.framedev.frameeconomy.mysql;

import de.framedev.frameeconomy.main.Main;
import de.framedev.frameeconomy.utils.PasswordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;


public class MySQL {

    private FileConfiguration cfg = Main.getInstance().getConfig();
    public static String MySQLPrefix = "§a[§bMySQL§a]";
    public static String host;
    public static String user;
    public static String password;
    public static String database;
    public static String port;
    public static Connection con;
    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    /**
     * Register this Class to use it
     */
    public MySQL() {
        host = cfg.getString("MySQL.Host");
        user = cfg.getString("MySQL.User");
        password = cfg.getString("MySQL.Password");
        database = cfg.getString("MySQL.Database");
        port = cfg.getString("MySQL.Port");
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        MySQL.host = host;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        MySQL.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MySQL.password = password;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String database) {
        MySQL.database = database;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        MySQL.port = port;
    }

    public static Connection getConnection() {
        if (con == null) {
            close();
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", user, password);
                con.setNetworkTimeout(Executors.newFixedThreadPool(100), 1000000);
                con.createStatement().executeUpdate("SET GLOBAL max_connections=1200;");
                return con;
            } catch (SQLException ex) {

            }
        } else {
            close();
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", user, password);
                con.setNetworkTimeout(Executors.newFixedThreadPool(100), 1000000);
                con.createStatement().executeUpdate("SET GLOBAL max_connections=1200;");
                return con;
            } catch (SQLException ex) {

            }
        }
        return con;
    }

    // connect
    public static void connect() {
        if (con == null) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", user, password);
                con.setNetworkTimeout(Executors.newFixedThreadPool(100), 1000000);
                con.createStatement().executeUpdate("SET GLOBAL max_connections=1200;");
                Bukkit.getConsoleSender().sendMessage(MySQLPrefix + "-Verbindung wurde aufgebaut!");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(MySQLPrefix + " §cEin Fehler ist aufgetreten: §a" + e.getMessage());
            }
        }
    }

    public static void close() {
        if (con != null) {
            try {
                if (con != null) {
                    con.close();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MySQLConnection implements Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = 7918204026312519949L;
		private String host;
        private String user;
        private String password;
        private String database;
        private String port;

        public MySQLConnection(String host, String user, String password, String database, String port) {
            this.host = host;
            this.user = user;
            String salt = PasswordUtils.getSalt(30);
            this.password = PasswordUtils.generateSecurePassword(password,salt);
            this.database = database;
            this.port = port;
        }

        @Override
        public String toString() {
            return "MySQLConnection{" +
                    "host=" + host +
                    ",user=" + user +
                    ",password=" + password +
                    ",database=" + database +
                    ",port=" + port +
                    "}";
        }

        public static MySQLConnection getFromString(String text) {
            text = text.replace("{","");
            text = text.replace("}","");
            text = text.replace("MySQLConnection","");
            String[] a = text.split(",");
            String host = a[0].replace("host=","");
            String user = a[1].replace("user=","");
            String password = a[2].replace("password=","");
            String database = a[3].replace("database=","");
            String port = a[4].replace("port=","");
            MySQL.setHost(host);
            MySQL.setUser(user);
            MySQL.setPassword(password);
            MySQL.setDatabase(database);
            MySQL.setPort(port);
            return new MySQLConnection(host,user,password,database,port);
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }
}

