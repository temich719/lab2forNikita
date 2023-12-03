package dao.impl;

import dao.AbstractDAO;
import dao.AdminDAO;
import pool.ConnectionPool;

public class AdminDAOImpl extends AbstractDAO implements AdminDAO {

    public AdminDAOImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }
}
