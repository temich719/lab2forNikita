package dao;

import dao.impl.*;
import pool.ConnectionPool;
import pool.DataBaseConfigReader;
import pool.impl.ConnectionPoolImpl;
import pool.impl.DataBaseConfigReaderImpl;

public class DAOFactory {

    private static final DataBaseConfigReader dataBaseConfigReader = new DataBaseConfigReaderImpl();
    private static final ConnectionPool connectionPool = new ConnectionPoolImpl(dataBaseConfigReader);

    private static final DAOFactory INSTANCE = new DAOFactory();

    private final AdminDAO adminDAO;
    private final ClientDAO clientDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final BillDAO billDAO;

    private DAOFactory() {
        this.adminDAO = new AdminDAOImpl(connectionPool);
        this.clientDAO = new ClientDAOImpl(connectionPool);
        this.productDAO = new ProductDAOImpl(connectionPool);
        this.orderDAO = new OrderDAOImpl(connectionPool);
        this.billDAO = new BillDAOImpl(connectionPool);
    }

    public static DAOFactory getINSTANCE() {
        return INSTANCE;
    }

    public AdminDAO getAdminDAO() {
        return adminDAO;
    }

    public BillDAO getBillDAO() {
        return billDAO;
    }

    public ClientDAO getClientDAO() {
        return clientDAO;
    }

    public OrderDAO getOrderDAO() {
        return orderDAO;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }
}
