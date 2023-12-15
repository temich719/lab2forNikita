package dao;

import exception.DAOException;
import model.Order;
import model.Product;

import java.util.List;

public interface ProductDAO {

    List<Product> getProducts() throws DAOException;

    Product getProductById(Long id) throws DAOException;

    List<Product> getProductsByOrder(Order order) throws DAOException;

    List<Product> findProductsByKeyword(String keyword) throws DAOException;

}
