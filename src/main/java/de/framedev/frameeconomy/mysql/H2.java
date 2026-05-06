package de.framedev.frameeconomy.mysql;

import de.framedev.frameeconomy.main.Main;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class H2 {

    private static Connection connection;
    private static String path;
    private static String fileName;
    private static String user;
    private static String password;

    public H2(String path, String fileName, String user, String password) {
        H2.path = path;
        H2.fileName = fileName;
        H2.user = user == null ? "sa" : user;
        H2.password = password == null ? "" : password;
    }

    public static Connection connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.h2.Driver");
            String url = getConnectionUrl();
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (ClassNotFoundException | SQLException exception) {
            logger().log(Level.SEVERE, "Could not connect to H2 database", exception);
        }
        return null;
    }

    @NotNull
    private static String getConnectionUrl() throws SQLException {
        File directory = new File(path == null ? "plugins/FrameEconomy/money" : path);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new SQLException("Could not create H2 database directory: " + directory.getAbsolutePath());
        }
        String databaseName = fileName == null || fileName.trim().isEmpty() ? "database" : fileName;
        String databasePath = new File(directory, databaseName).getPath().replace('\\', '/');
        return "jdbc:h2:file:" + databasePath + ";MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1";
    }

    /**
     * Connections stay open between operations and are closed during plugin shutdown.
     */
    public static void close() {
    }

    public static void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException exception) {
            logger().log(Level.WARNING, "Could not close H2 database connection", exception);
        } finally {
            connection = null;
        }
    }

    private static Logger logger() {
        Main plugin = Main.getInstance();
        return plugin != null ? plugin.getLogger() : Logger.getLogger(H2.class.getName());
    }
}
