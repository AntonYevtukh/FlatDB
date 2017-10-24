package yevtukh.anton.model.dao.implementations.jpa;

import yevtukh.anton.model.District;
import yevtukh.anton.model.Flat;
import yevtukh.anton.model.SearchParameters;
import yevtukh.anton.model.dao.interfaces.FlatsDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anton on 24.10.2017.
 */
public class JpaFlatsDao implements FlatsDao {

    private final EntityManager entityManager;

    public JpaFlatsDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void insertFlat(Flat flat) throws SQLException {
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(flat);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void insertFlats(List<Flat> flats) throws SQLException {
        entityManager.getTransaction().begin();
        try {
            for (Flat flat : flats) {
                entityManager.persist(flat);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public List<Flat> selectFlats(SearchParameters searchParameters) throws SQLException {

        Query query = entityManager.createQuery(
                "SELECT f FROM Flat f " +
                        "WHERE f.district IN :districts AND f.address LIKE CONCAT('%',:address,'%') AND " +
                        "f.rooms BETWEEN :from_rooms AND :to_rooms AND " +
                        "f.area BETWEEN :from_area AND :to_area AND " +
                        "f.price BETWEEN :from_price AND :to_price"
        );
        District district = searchParameters.getDistrict();
        List<District> districts = district == null ? Arrays.asList(District.values()) : Arrays.asList(district);
        query.setParameter("districts", districts);
        query.setParameter("address", searchParameters.getAddress());
        query.setParameter("from_rooms", searchParameters.getFromRooms());
        query.setParameter("to_rooms", searchParameters.getToRooms());
        query.setParameter("from_area", searchParameters.getFromArea());
        query.setParameter("to_area", searchParameters.getToArea());
        query.setParameter("from_price", searchParameters.getFromPrice());
        query.setParameter("to_price", searchParameters.getToPrice());
        return query.getResultList();
    }

    @Override
    public List<Flat> selectAllFlats() throws SQLException {
        Query query = entityManager.createNamedQuery("Flat.selectAll", Flat.class);
        return query.getResultList();
    }

    @Override
    public void close()
            throws SQLException {
        entityManager.close();
    }
}
