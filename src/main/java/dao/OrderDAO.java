package dao;

import exception.DAOException;
import model.Order;

import java.util.List;

public interface OrderDAO {

    Long makeOrder(Order order) throws DAOException;

    Order getOrderById(Long id) throws DAOException;

    List<Order> getOrdersByClientId(Long id) throws DAOException;

    List<Order> getApprovedOrders() throws DAOException;

}
