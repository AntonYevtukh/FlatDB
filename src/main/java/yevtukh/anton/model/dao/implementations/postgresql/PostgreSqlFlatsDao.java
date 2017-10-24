package yevtukh.anton.model.dao.implementations.postgresql;

import yevtukh.anton.model.District;
import yevtukh.anton.model.Flat;
import yevtukh.anton.model.SearchParameters;
import yevtukh.anton.model.dao.interfaces.FlatsDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 14.10.2017.
 */
public class PostgreSqlFlatsDao implements FlatsDao {

    private final Connection connection;

    public PostgreSqlFlatsDao(Connection connection) {
        this.connection = connection;
    }

    public void insertFlat(Flat flat)
            throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO Flats (district, address, rooms, area, price) VALUES (CAST(? AS districts), ?, ?, ?, ?)"
        )) {
            preparedStatement.setString(1, flat.getDistrict().name());
            preparedStatement.setString(2, flat.getAddress());
            preparedStatement.setInt(3, flat.getRooms());
            preparedStatement.setInt(4, flat.getArea());
            preparedStatement.setInt(5, flat.getPrice());
            preparedStatement.executeUpdate();
        }
    }

    public void insertFlats(List<Flat> flats)
            throws SQLException {
        for (Flat flat : flats)
            insertFlat(flat);
    }

    public List<Flat> selectFlats(SearchParameters searchParameters)
            throws SQLException {
        //Ничего умнее не придумал, enum в Postgres зло
        String range = searchParameters.getDistrict() == null ? District.toSqlEnumString() :
                "('" + searchParameters.getDistrict().name() + "')";
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM Flats " +
                        "WHERE district IN " + range + " AND address LIKE ? AND rooms BETWEEN ? AND ? AND " +
                        "area BETWEEN ? AND ? AND price BETWEEN ? AND ?"

        )) {
            preparedStatement.setString(1, "%" + searchParameters.getAddress() + "%");
            preparedStatement.setInt(2, searchParameters.getFromRooms());
            preparedStatement.setInt(3, searchParameters.getToRooms());
            preparedStatement.setInt(4, searchParameters.getFromArea());
            preparedStatement.setInt(5, searchParameters.getToArea());
            preparedStatement.setInt(6, searchParameters.getFromPrice());
            preparedStatement.setInt(7, searchParameters.getToPrice());

            System.out.println(preparedStatement);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return getFlatsFromResultSet(resultSet);
            }
        }
    }

    public List<Flat> selectAllFlats()
            throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Flats ")) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return getFlatsFromResultSet(resultSet);
            }
        }
    }

    private List<Flat> getFlatsFromResultSet(ResultSet resultSet)
            throws SQLException {
        List<Flat> flats = new ArrayList<>();
        while (resultSet.next()) {
            District district = District.valueOf(resultSet.getString(2));
            String address = resultSet.getString(3);
            int rooms = resultSet.getInt(4);
            int area = resultSet.getInt(5);
            int price = resultSet.getInt(6);
            flats.add(new Flat(district, address, rooms, area, price));
        }
        return flats;
    }

    public void close()
            throws SQLException {
        if (connection != null)
            connection.close();
    }
}
