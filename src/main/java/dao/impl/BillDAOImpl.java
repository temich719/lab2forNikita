package dao.impl;

import dao.AbstractDAO;
import dao.BillDAO;
import exception.DAOException;
import model.Bill;
import model.Order;
import model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pool.ConnectionPool;
import sessionJPA.SessionFactoryCreator;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl extends AbstractDAO implements BillDAO {

    private final SessionFactory sessionFactory = SessionFactoryCreator.getSessionFactory();

    private final Logger LOGGER = LogManager.getLogger(BillDAOImpl.class);

    public BillDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Bill makeBill(Order order) throws DAOException {
        LOGGER.info("create bill");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Bill bill = new Bill();
            List<Order> orders = new ArrayList<>();
            orders.add(order);
            bill.setOrders(orders);
            bill.setTotal(calculateTotal(order));
            session.save(bill);
            transaction.commit();
            return bill;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    private Double calculateTotal(Order order) {
        LOGGER.info("Calculating total bill");
        Double total = 0.0;
        for (Product product : order.getProducts()) {
            total += product.getPrice();
        }
        return total;
    }
}
