package dao.impl;

import dao.*;
import exception.DAOException;
import model.Order;
import model.Order_;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pool.ConnectionPool;
import sessionJPA.SessionFactoryCreator;

import javax.persistence.criteria.*;
import java.util.List;

public class OrderDAOImpl extends AbstractDAO implements OrderDAO {

    private final SessionFactory sessionFactory = SessionFactoryCreator.getSessionFactory();

    private final Logger LOGGER = LogManager.getLogger(OrderDAOImpl.class);

    public OrderDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }


    @Override
    public Long makeOrder(Order order) throws DAOException {
        LOGGER.info("Make order");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
            return getLastOrderId();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public Order getOrderById(Long id) throws DAOException {
        LOGGER.info("Get order by id");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
            Root<Order> root = criteriaQuery.from(Order.class);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get(Order_.orderNumber), id));
            return session.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public List<Order> getOrdersByClientId(Long id) throws DAOException {
        LOGGER.info("Getting orders by client id");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> query = builder.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);
            query.select(root).where(builder.equal(root.get(Order_.ORDER_NUMBER), id));
            List<Order> orders = session.createQuery(query).getResultList();
            transaction.commit();
            return orders;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public List<Order> getApprovedOrders() throws DAOException {
        LOGGER.info("Getting approved orders");
        ProductDAO productDAO = DAOFactory.getINSTANCE().getProductDAO();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> query = builder.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);
            Predicate condition = builder.equal(root.get(Order_.IS_APPROVED), true);
            query.select(root).where(condition);
            Query<Order> q = session.createQuery(query);
            List<Order> orders = q.getResultList();
            for (Order order : orders) {
                order.setProducts(productDAO.getProductsByOrder(order));
            }
            transaction.commit();
            return orders;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    private Long getLastOrderId() throws DAOException {
        LOGGER.info("Get last order id");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Order> root = query.from(Order.class);
            query.select(root.get(Order_.ORDER_NUMBER))
                    .orderBy(builder.desc(root.get(Order_.ORDER_NUMBER)));
            Query<Long> q = session.createQuery(query);
            q.setMaxResults(1);
            List<Long> result = q.getResultList();
            transaction.commit();
            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }
}
