package yevtukh.anton.database;

import yevtukh.anton.database.initializers.DbInitialiser;
import yevtukh.anton.database.initializers.MySqlInitializer;
import yevtukh.anton.database.initializers.PostgreSqlInitializer;
import yevtukh.anton.model.dao.implementations.jpa.JpaFlatsDao;
import yevtukh.anton.model.dao.implementations.mysql.MySqlFlatsDao;
import yevtukh.anton.model.dao.implementations.postgresql.PostgreSqlFlatsDao;
import yevtukh.anton.model.dao.interfaces.FlatsDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by Anton on 14.10.2017.
 */
public class DbWorker {

    private final String DB_CONNECTION;
    private final String DB_USER;
    private final String DB_PASSWORD;
    private final String DBMS_NAME;
    private boolean JPA_USE;
    private final EntityManagerFactory ENTITY_MANAGER_FACTORY;
    private final boolean DROP;
    private static DbWorker instance;

    public static DbWorker getInstance() {
        if (instance == null) {
            try {
                instance = new DbWorker();
            } catch (Exception e) {
                System.out.println("Can't get instance of DbWorker");
                e.printStackTrace();
            }
        }
        return instance;
    }

    private DbWorker()
            throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        DBMS_NAME = properties.getProperty("dbms.name");
        DROP = "1".equals(properties.getProperty("db.drop"));
        JPA_USE = "1".equals(properties.getProperty("jpa.use"));
        boolean runtimeConfig = "1".equals(properties.getProperty("db.runtime_config"));

        if (!runtimeConfig) {
            DB_CONNECTION = properties.getProperty("db.url");
            DB_USER = properties.getProperty("db.user");
            DB_PASSWORD = properties.getProperty("db.password");
        } else {
            DB_CONNECTION = System.getenv(properties.getProperty("system.db.url"));
            DB_USER = System.getenv(properties.getProperty("system.db.user"));
            DB_PASSWORD = System.getenv(properties.getProperty("system.db.password"));
        }

        if (JPA_USE) {
            if (runtimeConfig) {
                properties.put("javax.persistence.jdbc.url", DB_CONNECTION);
                properties.put("javax.persistence.jdbc.user", DB_USER);
                properties.put("javax.persistence.jdbc.password", DB_PASSWORD);
            }
            ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory(DBMS_NAME, properties);
        }
        else
            ENTITY_MANAGER_FACTORY = null;
    }

    public void initDB()
            throws SQLException, ClassNotFoundException {
        if (!JPA_USE) {
            getDbInitializer().initDB(DROP);
        }
        fillDb();
    }

    public Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    }

    public FlatsDao createFlatsDao()
            throws SQLException {
        if (JPA_USE)
            return new JpaFlatsDao(ENTITY_MANAGER_FACTORY.createEntityManager());
        switch (DBMS_NAME) {
            case "mysql":
                return new MySqlFlatsDao(getConnection());
            case "postgresql":
                return new PostgreSqlFlatsDao(getConnection());
            default:
                throw new SQLException("Database management system is not supported");
        }
    }

    public void fillDb()
            throws SQLException, ClassNotFoundException {
        if (DROP && InitData.INITIAL_FLATS != null) {
            try(FlatsDao flatsDao = createFlatsDao()) {
                flatsDao.insertFlats(InitData.INITIAL_FLATS);
            }
        }
    }

    public DbInitialiser getDbInitializer()
            throws SQLException, ClassNotFoundException {
        switch (DBMS_NAME) {
            case "mysql":
                return new MySqlInitializer();
            case "postgresql":
                return new PostgreSqlInitializer();
            default:
                throw new SQLException("Database management system is not supported");
        }
    }

    public void stop() {
        if (ENTITY_MANAGER_FACTORY != null)
            ENTITY_MANAGER_FACTORY.close();
    }
}
