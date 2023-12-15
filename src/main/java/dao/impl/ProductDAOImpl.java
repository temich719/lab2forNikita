package dao.impl;

import dao.AbstractDAO;
import dao.ProductDAO;
import exception.DAOException;
import model.*;
import model.Order;
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

public class ProductDAOImpl extends AbstractDAO implements ProductDAO {

    private final Logger LOGGER = LogManager.getLogger(ProductDAOImpl.class);
    private final SessionFactory sessionFactory = SessionFactoryCreator.getSessionFactory();

    public ProductDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public List<Product> getProducts() throws DAOException {
        LOGGER.info("Select all products");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            criteriaQuery.select(root);
            transaction.commit();
            return session.createQuery(criteriaQuery).list();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public Product getProductById(Long id) throws DAOException {
        LOGGER.info("Select product by id");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(Product_.ID), id));
            transaction.commit();
            return session.createQuery(criteriaQuery).uniqueResult();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public List<Product> getProductsByOrder(Order order) throws DAOException {
        LOGGER.info("Get product by order");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> query = builder.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);
            Join<Product, Order> join = root.join("orders");

            Predicate condition = builder.equal(join.get("orderNumber"), order.getOrderNumber());
            query.select(root).where(condition);

            Query<Product> q = session.createQuery(query);
            transaction.commit();
            return q.getResultList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public List<Product> findProductsByKeyword(String keyword) throws DAOException {
        LOGGER.info("Select products by keyword");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            criteriaQuery.select(root).where(criteriaBuilder.like(root.get(Product_.NAME), "%" + keyword + "%"));
            Query<Product> q = session.createQuery(criteriaQuery);
            transaction.commit();
            return q.getResultList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }
}
