package sessionJPA;

import model.Bill;
import model.Client;
import model.Order;
import model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Objects;

public class SessionFactoryCreator {

    private static SessionFactory sessionFactory;

    private final static Logger LOGGER = LogManager.getLogger(SessionFactoryCreator.class);

    private static void configure() {
        try {
            LOGGER.info("Create hibernate session");
            sessionFactory = new Configuration()
                    .addAnnotatedClass(Bill.class)
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(Product.class)
                    .configure().buildSessionFactory();
        } catch (Throwable e) {
            LOGGER.error("Failed to create sessionFactory object. " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (Objects.isNull(sessionFactory)) {
            configure();
        }
        return sessionFactory;
    }

}
