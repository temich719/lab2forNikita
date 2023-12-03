package pool.impl;

import exception.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pool.ConnectionPool;
import pool.DataBaseConfigReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static util.DAOFinalsStorage.*;

public class ConnectionPoolImpl implements ConnectionPool {

    private final Logger LOGGER = LogManager.getLogger(ConnectionPoolImpl.class);
    private final String DRIVER;
    private static boolean driverIsLoaded = false;

    private final BlockingQueue<Connection> availableConnections;
    private final BlockingQueue<Connection> takenConnections;


    public ConnectionPoolImpl(DataBaseConfigReader databaseConfigReader) {
        this.DRIVER = databaseConfigReader.get(SQL_DRIVER);
        String url = databaseConfigReader.get(DB_URL);
        String user = databaseConfigReader.get(DB_LOGIN);
        String password = databaseConfigReader.get(DB_PASSWORD);
        int amountOfConnections = 50;
        this.availableConnections = new ArrayBlockingQueue<>(amountOfConnections);
        this.takenConnections = new ArrayBlockingQueue<>(amountOfConnections);

        try {
            getJDBCDriver();
            for (int i = 0; i < amountOfConnections; i++) {
                availableConnections.add(getConnection(url, user, password));
            }
        } catch (DAOException e) {
            LOGGER.info("availableConnections.size() is " + availableConnections.size());
            LOGGER.info("takenConnections.size() is " + takenConnections.size());
        }
    }

    @Override
    public synchronized Connection provide() throws DAOException {
        Connection newConnection;
        try {
            newConnection = availableConnections.take();
            takenConnections.add(newConnection);
        } catch (InterruptedException e) {
            throw new DAOException(e);
        }
        LOGGER.info("ConnectionPool.provide()");
        LOGGER.info("ConnectionPool.availableConnections.size() is " + availableConnections.size());
        LOGGER.info("ConnectionPool.takenConnections.size() is " + takenConnections.size());
        return newConnection;
    }

    @Override
    public synchronized void retrieve(Connection connection) {
        if (connection != null) {
            takenConnections.remove(connection);
            availableConnections.add(connection);
        } else {
            LOGGER.info("ConnectionPool.retrieve(Connection connection)");
            LOGGER.info("connection is NULL");
        }
        LOGGER.info("ConnectionPool.availableConnections.size() is " + availableConnections.size());
        LOGGER.info("ConnectionPool.takenConnections.size() is " + takenConnections.size());
    }

    private Connection getConnection(String url, String user, String password) throws DAOException {
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.error("Could not connect to database");
            throw new DAOException("Connection to database failed", e);
        }
        return connection;
    }

    private void getJDBCDriver() throws DAOException {
        if (!driverIsLoaded) {
            try {
                Class.forName(DRIVER);
                driverIsLoaded = true;
                LOGGER.info("MySQL driver is loaded");
            } catch (ClassNotFoundException e) {
                LOGGER.error("MySQL driver is not loaded");
                throw new DAOException("MySQL driver is not loaded", e);
            }
        }
    }

}
