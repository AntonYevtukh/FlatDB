package yevtukh.anton.database.initializers;

import yevtukh.anton.database.DbWorker;
import yevtukh.anton.model.District;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Anton on 17.10.2017.
 */
public class MySqlInitializer implements DbInitialiser {

    @Override
    public void initDB(boolean drop)
            throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DbWorker.getInstance().getConnection();
        try(Statement statement = connection.createStatement()){
            if (drop) {
                statement.execute("DROP TABLE IF EXISTS FLATS");
            }
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Flats (" +
                            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "district ENUM " + District.toSqlEnumString() + " NOT NULL," +
                            "address VARCHAR(50) NOT NULL," +
                            "    rooms INT NOT NULL," +
                            "    area INT NOT NULL," +
                            "    price INT NOT NULL" +
                            ");");
        }
    }
}
