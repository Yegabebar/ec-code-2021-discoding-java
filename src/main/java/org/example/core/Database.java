package org.example.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton to access the SQL Database.
 * When you need to execute an SQL query, always call getConnection().
 */
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private static Database instance;
    private Connection connection;

    public static Database get() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Private constructor
    private Database() {
    }

    /**
     * Retrieves database metadata to make sure that the connection is properly established.
     */
    public void checkConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                DatabaseMetaData meta = conn.getMetaData();
                logger.info("The driver name is " + meta.getDriverName());
                logger.info("Connection to database up and running!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves a database connection to execute SQL queries.
     * Because the same Connection instance is used throughout the app lifetime,
     * you do not have to close the connection at the end of your query.
     * @return a valid Connection instance.
     */
    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            connection = DriverManager.getConnection(Conf.DB_URL, Conf.DB_USER, Conf.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
