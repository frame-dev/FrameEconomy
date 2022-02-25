package de.framedev.frameeconomy.utils;

import de.framedev.frameeconomy.main.Main;
import de.framedev.frameeconomy.mongodb.BackendManager;
import de.framedev.frameeconomy.mongodb.MongoManager;

import java.util.logging.Level;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.utils
 * ClassName MongoDBUtils
 * Date: 07.04.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class MongoDBUtils {

    private MongoManager mongoManager;
    private BackendManager backendManager;

    public MongoDBUtils() {
        if (Main.getInstance().getConfig().getBoolean("MongoDB.Localhost")) {
            this.mongoManager = new MongoManager();
            mongoManager.connectLocalHost();
            Main.getInstance().getLogger().log(Level.INFO, "MongoDB Enabled!");
            this.backendManager = new BackendManager(Main.getInstance());
        } else if (Main.getInstance().getConfig().getBoolean("MongoDB.Normal")) {
            this.mongoManager = new MongoManager();
            mongoManager.connect();
            Main.getInstance().getLogger().log(Level.INFO, "MongoDB Enabled!");
            this.backendManager = new BackendManager(Main.getInstance());
        }
    }

    /**
     * this Method will be returning the initialzed MongoManager
     * @return returns the initialzed MongoManager
     */
    public MongoManager getMongoManager() {
        return mongoManager;
    }

    /**
     * this Method will be returning the initialzed BackendManager
     * @return returns the initialzed BackendManager
     */
    public BackendManager getBackendManager() {
        return backendManager;
    }
}
