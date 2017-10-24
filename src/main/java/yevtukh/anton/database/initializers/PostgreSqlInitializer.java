package yevtukh.anton.database.initializers;

import yevtukh.anton.database.DbWorker;
import yevtukh.anton.model.District;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Anton on 17.10.2017.
 */
public class PostgreSqlInitializer implements DbInitialiser{

    @Override
    public void initDB(boolean drop)
            throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DbWorker.getInstance().getConnection();
        try(Statement statement = connection.createStatement()){
            if (drop) {
                statement.execute("DROP TABLE IF EXISTS Flats");
            }

            //Проще нельзя, видимо
            statement.execute(String.format("DO " +
                            "$$BEGIN " +
                                "IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'districts') THEN " +
                                    "CREATE TYPE districts AS ENUM %s" +
                                "; END IF; " +
                            "END$$;", District.toSqlEnumString()));
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Flats (" +
                            "id SERIAL PRIMARY KEY," +
                            "district districts NOT NULL," +
                            "address VARCHAR(50) NOT NULL," +
                            "    rooms INT NOT NULL," +
                            "    area INT NOT NULL," +
                            "    price INT NOT NULL" +
                            ");");
        }
    }
}
