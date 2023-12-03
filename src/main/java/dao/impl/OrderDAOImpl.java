package dao.impl;

import dao.*;
import exception.DAOException;
import model.Client;
import model.Order;
import model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl extends AbstractDAO implements OrderDAO {

    private ClientDAO clientDAO;
    private ProductDAO productDAO;

    private final Logger LOGGER = LogManager.getLogger(OrderDAOImpl.class);

    private static final String MAKE_ORDER = "INSERT INTO orders (isApproved, clientId) VALUES (?,?);";
    private static final String GET_LAST_ORDER_ID = "SELECT orderNumber FROM orders ORDER BY orderNumber DESC LIMIT 1";
    private static final String INSERT_ORDERS_PRODUCTS = "INSERT INTO orders_products (orderId, productId) VALUES (?, ?)";
    private static final String GET_ORDER_BY_ID = "SELECT * FROM orders WHERE orderNumber = ?;";
    private static final String GET_ORDERS_BY_CLIENT_ID = "SELECT * FROM orders WHERE clientId = ?;";
    private static final String GET_APPROVED_ORDERS = "SELECT * FROM orders WHERE isApproved = 1;";

    public OrderDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }


    @Override
    public synchronized Integer makeOrder(Order order) throws DAOException {
        LOGGER.info("Make order");
        clientDAO = DAOFactory.getINSTANCE().getClientDAO();
        productDAO = DAOFactory.getINSTANCE().getProductDAO();
        Connection connection = null;
        PreparedStatement insertStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            insertStatement = connection.prepareStatement(MAKE_ORDER);
            insertStatement.setBoolean(1, order.getApproved());
            insertStatement.setInt(2, order.getClient().getId());
            insertStatement.executeUpdate();
            connection.commit();
            Integer id = getLastOrderId();
            for(Product product : order.getProducts()) {
                insertProductsOrders(id, product.getId());
            }
            return id;
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(insertStatement);
            connectionPool.retrieve(connection);
        }
    }

    @Override
    public synchronized Order getOrderById(Integer id) throws DAOException {
        LOGGER.info("Get order by id");
        clientDAO = DAOFactory.getINSTANCE().getClientDAO();
        productDAO = DAOFactory.getINSTANCE().getProductDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_ORDER_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Order order = new Order();
            resultSet.next();
            order.setOrderNumber(resultSet.getInt(1));
            order.setApproved(resultSet.getBoolean(2));
            Integer clientId = resultSet.getInt(3);
            Client client = clientDAO.getClientById(clientId);
            order.setClient(client);
            List<Product> products = productDAO.getProductsByOrder(order);
            order.setProducts(products);
            connection.commit();
            return order;
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    @Override
    public synchronized List<Order> getOrdersByClientId(Integer id) throws DAOException {
        LOGGER.info("Getting orders by client id");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        clientDAO = DAOFactory.getINSTANCE().getClientDAO();
        productDAO = DAOFactory.getINSTANCE().getProductDAO();
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_ORDERS_BY_CLIENT_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.commit();
            List<Order> orders = new ArrayList<>();
            Order order;
            while (resultSet.next()) {
                order = new Order();
                order.setOrderNumber(resultSet.getInt(1));
                order.setApproved(resultSet.getBoolean(2));
                order.setClient(clientDAO.getClientById(resultSet.getInt(3)));
                order.setProducts(productDAO.getProductsByOrder(order));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    @Override
    public synchronized List<Order> getApprovedOrders() throws DAOException {
        LOGGER.info("Getting approved orders");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        clientDAO = DAOFactory.getINSTANCE().getClientDAO();
        productDAO = DAOFactory.getINSTANCE().getProductDAO();
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_APPROVED_ORDERS);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.commit();
            List<Order> orders = new ArrayList<>();
            Order order;
            while (resultSet.next()) {
                order = new Order();
                order.setOrderNumber(resultSet.getInt(1));
                order.setApproved(resultSet.getBoolean(2));
                order.setClient(clientDAO.getClientById(resultSet.getInt(3)));
                order.setProducts(productDAO.getProductsByOrder(order));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    private synchronized Integer getLastOrderId() throws DAOException {
        LOGGER.info("Get last order id");
        clientDAO = DAOFactory.getINSTANCE().getClientDAO();
        productDAO = DAOFactory.getINSTANCE().getProductDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_LAST_ORDER_ID);
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

    private synchronized void insertProductsOrders(Integer orderId, Integer productId) throws DAOException {
        LOGGER.info("Insert into junction table");
        clientDAO = DAOFactory.getINSTANCE().getClientDAO();
        productDAO = DAOFactory.getINSTANCE().getProductDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_ORDERS_PRODUCTS);
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }
}
