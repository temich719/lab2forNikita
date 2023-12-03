package dao;

import exception.DAOException;
import model.Bill;
import model.Order;

public interface BillDAO {

    Bill makeBill(Order order) throws DAOException;

}
