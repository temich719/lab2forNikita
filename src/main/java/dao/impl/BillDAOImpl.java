package dao.impl;

import dao.AbstractDAO;
import dao.BillDAO;
import dao.DAOFactory;
import dao.OrderDAO;
import exception.DAOException;
import model.Bill;
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

public class BillDAOImpl extends AbstractDAO implements BillDAO {

    private OrderDAO orderDAO;

    private final Logger LOGGER = LogManager.getLogger(BillDAOImpl.class);

    private static final String INSERT_BILL = "INSERT INTO bills() VALUES ();";
    private static final String GET_LAST_BILL_ID = "SELECT id FROM bills ORDER BY id DESC LIMIT 1;";
    private static final String INSERT_ORDERS_BILLS = "INSERT INTO bills_orders (billId, orderId) VALUES (?, ?);";

    public BillDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public synchronized Bill makeBill(Order order) throws DAOException {
        LOGGER.info("create bill");
        orderDAO = DAOFactory.getINSTANCE().getOrderDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_BILL);
            preparedStatement.executeUpdate();
            connection.commit();
            Integer id = getLastId();
            insertBillsOrders(id, order.getOrderNumber());
            Order order1 = orderDAO.getOrderById(id);
            Bill bill = new Bill();
            List<Order> orders = new ArrayList<>();
            orders.add(order1);
            bill.setOrders(orders);
            bill.setTotal(calculateTotal(order));
            return bill;
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    private synchronized Integer getLastId() throws DAOException {
        LOGGER.info("Get last bill id");
        orderDAO = DAOFactory.getINSTANCE().getOrderDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(GET_LAST_BILL_ID);
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

    private synchronized void insertBillsOrders(Integer billId, Integer orderId) throws DAOException {
        LOGGER.info("Insert into junction table");
        orderDAO = DAOFactory.getINSTANCE().getOrderDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_ORDERS_BILLS);
            preparedStatement.setInt(1, billId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DAOException("Error in DAO method", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    private synchronized Double calculateTotal(Order order) {
        LOGGER.info("Calculating total bill");
        Double total = 0.0;
        for (Product product : order.getProducts()) {
            total += product.getPrice();
        }
        return total;
    }
}
