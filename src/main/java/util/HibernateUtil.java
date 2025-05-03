package util;


import entity.Currency;
import entity.Item;
import entity.Realm;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.util.Properties;

public class HibernateUtil {

    private HibernateUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Load DB credentials from external properties file
            Properties props = new Properties();
            props.load(new FileInputStream("./src/main/resources/password.properties"));

            props.setProperty("hibernate.connection.username", props.getProperty("username"));
            props.setProperty("hibernate.connection.password", props.getProperty("password"));

            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Currency.class)
                    .addAnnotatedClass(Item.class)
                    .addAnnotatedClass(Realm.class)
                    .addProperties(props)
                    .buildSessionFactory();
        } catch (Exception ex) {
            System.out.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
