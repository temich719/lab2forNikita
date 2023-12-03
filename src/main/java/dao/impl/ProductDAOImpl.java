package dao.impl;

import dao.AbstractDAO;
import dao.ProductDAO;
import exception.DAOException;
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

public class ProductDAOImpl extends AbstractDAO implements ProductDAO {

    private final Logger LOGGER = LogManager.getLogger(ProductDAOImpl.class);

    private static final String SELECT_PRODUCTS = "SELECT * FROM Products;";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM Products WHERE id = ?;";
    private static final String SELECT_PRODUCTS_BY_ORDER_ID = "SELECT productId FROM orders_products WHERE orderId = ?;";
    private static final String SELECT_PRODUCTS_BY_KEYWORD = "SELECT * FROM Products WHERE name LIKE ?;";

    public ProductDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public List<Product> getProducts() throws DAOException {
        LOGGER.info("Select all products");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Product> products = new ArrayList<>();
        try {
            connection = connectionPool.provide();
            preparedStatement = connection.prepareStatement(SELECT_PRODUCTS);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(resultSet.getString(2), resultSet.getDouble(3), resultSet.getString(4));
                products.add(product);
            }
        } catch (SQLException e) {
            throw new DAOException("DAO exception", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
        return products;
    }

    @Override
    public Product getProductById(int id) throws DAOException {
        LOGGER.info("Select product by id");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Product product;
        try {
            connection = connectionPool.provide();
            preparedStatement = connection.prepareStatement(SELECT_PRODUCT_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            product = new Product(resultSet.getString(2), resultSet.getDouble(3), resultSet.getString(4));
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(resultSet, preparedStatement);
            connectionPool.retrieve(connection);
        }
        return product;
    }

    @Override
    public List<Product> getProductsByOrder(Order order) throws DAOException {
        LOGGER.info("Get product by order");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Product> products = new ArrayList<>();
        try {
            connection = connectionPool.provide();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SELECT_PRODUCTS_BY_ORDER_ID);
            preparedStatement.setInt(1, order.getOrderNumber());
            resultSet = preparedStatement.executeQuery();
            List<Integer> productsIds = new ArrayList<>();
            while (resultSet.next()) {
                Integer id = resultSet.getInt(1);
                productsIds.add(id);
            }
            for (Integer productId : productsIds) {
                Product product = getProductById(productId);
                products.add(product);
            }
            connection.commit();
            return products;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(resultSet, preparedStatement);
            connectionPool.retrieve(connection);
        }
    }

    @Override
    public List<Product> findProductsByKeyword(String keyword) throws DAOException {
        LOGGER.info("Select products by keyword");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Product> products = new ArrayList<>();
        try {
            connection = connectionPool.provide();
            preparedStatement = connection.prepareStatement(SELECT_PRODUCTS_BY_KEYWORD);
            keyword = "%" + keyword + "%";
            preparedStatement.setString(1, keyword);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(resultSet.getString(2), resultSet.getDouble(3), resultSet.getString(4));
                products.add(product);
            }
        } catch (SQLException e) {
            throw new DAOException("DAO exception", e);
        } finally {
            close(preparedStatement);
            connectionPool.retrieve(connection);
        }
        return products;
    }
}
