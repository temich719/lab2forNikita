package dao.impl;

import dao.AbstractDAO;
import dao.ClientDAO;
import exception.DAOException;
import model.Client;
import model.Client_;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pool.ConnectionPool;
import sessionJPA.SessionFactoryCreator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ClientDAOImpl extends AbstractDAO implements ClientDAO {

    private final SessionFactory sessionFactory = SessionFactoryCreator.getSessionFactory();

    private final Logger LOGGER = LogManager.getLogger(ClientDAOImpl.class);

    public ClientDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Long createClient(Client client) throws DAOException {
        LOGGER.info("Create user");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            return client.getId();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }

    @Override
    public Client getClientById(Long id) throws DAOException {
        LOGGER.info("Get user by id");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Client> criteriaQuery = criteriaBuilder.createQuery(Client.class);
            Root<Client> root = criteriaQuery.from(Client.class);
            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(Client_.ID), id));
            Client client = session.createQuery(criteriaQuery).getSingleResult();
            transaction.commit();
            return client;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error in DAO method", e);
        }
    }
}
