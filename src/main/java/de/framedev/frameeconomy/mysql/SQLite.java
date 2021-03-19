package de.framedev.frameeconomy.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + path + "/" + fileName + ".db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            connection = conn;
            return conn;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
