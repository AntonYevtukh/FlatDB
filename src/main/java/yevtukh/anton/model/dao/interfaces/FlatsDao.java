package yevtukh.anton.model.dao.interfaces;

import yevtukh.anton.model.Flat;
import yevtukh.anton.model.SearchParameters;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Anton on 21.10.2017.
 */
public interface FlatsDao extends AutoCloseable {
    void insertFlat(Flat flat) throws SQLException;
    void insertFlats(List<Flat> flats) throws SQLException;
    List<Flat> selectFlats(SearchParameters searchParameters) throws SQLException;
    List<Flat> selectAllFlats() throws SQLException;
    void close() throws SQLException;
}
