package exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DAOException extends Exception{

    private final static Logger LOGGER = LogManager.getLogger(DAOException.class);

    public DAOException() {
        super();
        LOGGER.error("ERROR_OCCUPIED");
    }

    public DAOException(String message) {
        super(message);
        LOGGER.error(message);
    }

    public DAOException(Exception e) {
        super(e);
        LOGGER.error(e);
    }

    public DAOException(String message, Exception e) {
        super(message, e);
        LOGGER.error(message, e);
    }
}
