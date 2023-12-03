package dao;

import exception.DAOException;
import model.Order;

import java.util.List;

public interface OrderDAO {

    Integer makeOrder(Order order) throws DAOException;

    Order getOrderById(Integer id) throws DAOException;

    List<Order> getOrdersByClientId(Integer id) throws DAOException;

    List<Order> getApprovedOrders() throws DAOException;

}
