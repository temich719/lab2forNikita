package dao.impl;

import dao.AbstractDAO;
import dao.ClientDAO;
import dao.DAOFactory;
import exception.DAOException;
import model.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAOImpl extends AbstractDAO implements ClientDAO {

    private final Logger LOGGER = LogManager.getLogger(ClientDAOImpl.class);

    private static final String CREATE_CLIENT = "INSERT INTO clients (name, surname, money) VALUES (?, ?, ?);";
    private static final String GET_CLIENT_BY_ID = "SELECT * FROM clients WHERE id = ?;";
    private static final String GET_LAST_CLIENT_ID = "SELECT id FROM clients ORDER BY id DESC LIMIT 1;";

    public ClientDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public synchronized Integer createClient(Client client) throws DAOException {
        LOGGER.info("Create user");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(CREATE_CLIENT);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getSurname());
            preparedStatement.setDouble(3, client.getMoney());
            preparedStatement.executeUpdate();
            Integer id = getLastId();
            connection.commit();
            return id;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    @Override
    public synchronized Client getClientById(Integer id) throws DAOException {
        LOGGER.info("Get user by id");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_CLIENT_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.commit();
            Client client = new Client();
            resultSet.next();
            client.setId(resultSet.getInt(1));
            client.setName(resultSet.getString(2));
            client.setSurname(resultSet.getString(3));
            client.setMoney(resultSet.getDouble(4));
            return client;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    private Integer getLastId() throws DAOException {
        LOGGER.info("Get last client id");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_LAST_CLIENT_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.commit();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }
}
