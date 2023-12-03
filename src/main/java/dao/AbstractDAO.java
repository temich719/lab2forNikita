package dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pool.ConnectionPool;

import java.util.Objects;

public abstract class AbstractDAO {

    private final Logger LOGGER = LogManager.getLogger(AbstractDAO.class);

    protected static ConnectionPool connectionPool;

    public AbstractDAO(ConnectionPool connectionPool1) {
        connectionPool = connectionPool1;
    }

    protected void close(AutoCloseable... autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (Objects.nonNull(autoCloseable)) {
                try {
                    autoCloseable.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

}
