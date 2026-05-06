package de.framedev.frameeconomy.mysql;

import de.framedev.frameeconomy.main.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.mysql
 * Date: 08.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class SQLite {

    public static Connection connection;
    private static String fileName;
    private static String path;

    public SQLite(String path, String fileName) {
        SQLite.fileName = fileName;
        SQLite.path = path;
    }

    /**
     * Connect to the SQLite Database
     * @return the Connected Connection
     */
    public static Connection connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + path + "/" + fileName + ".db";
            // create a connection to the database
            connection = DriverManager.getConnection(url);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            logger().log(Level.SEVERE, "Could not connect to SQLite database", e);
        }
        return null;
    }

    /**
     * Closes the Opened Connection
     */
    public static void close() {
        // Connections are reused by FrameEconomy's serialized database worker.
    }

    public static void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException ex) {
                logger().log(Level.WARNING, "Could not close SQLite database connection", ex);
            }
        }
    }

    private static Logger logger() {
        Main plugin = Main.getInstance();
        return plugin != null ? plugin.getLogger() : Logger.getLogger(SQLite.class.getName());
    }
}
