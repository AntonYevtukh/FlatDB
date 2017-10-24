package yevtukh.anton.controllers;

import yevtukh.anton.database.DbWorker;
import yevtukh.anton.model.*;
import yevtukh.anton.model.dao.interfaces.FlatsDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by Anton on 14.10.2017.
 */
public class FlatsController extends HttpServlet {

    private static final DbWorker DB_WORKER = DbWorker.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("districts", District.values());
        switch (req.getParameter("action")) {
            case "add":
                addFlat(req, resp);
                break;
            case "get":
                getFlats(req, resp);
                break;
            default:
                resp.setStatus(400);
                resp.sendRedirect("");
        }
    }

    private void addFlat(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        try (FlatsDao flatsDao = DB_WORKER.createFlatsDao()){
            req.setAttribute("select", "All Flats");
            Flat flat = parseFlat(req);
            SearchParameters searchParameters = parseSearchParameters(req);
            setPreviousRequestParameters(req, flat, searchParameters);
            if (!validateFlat(flat)) {
                req.setAttribute("error_message", "Invalid flat data, please try again");
                req.getRequestDispatcher("/WEB-INF/views/flats.jsp").forward(req, resp);
                return;
            }
            flatsDao.insertFlat(flat);
            List<Flat> flats = flatsDao.selectAllFlats();
            req.setAttribute("success_message", "Flat successfully added");
            req.setAttribute("flats", flats);
        } catch (SQLException e) {
            req.setAttribute("error_message", "Internal SQL error");
            System.err.println("Unable to insert the Flat into table");
            e.printStackTrace();
        }
        req.getRequestDispatcher("/WEB-INF/views/flats.jsp").forward(req, resp);
    }

    private void getFlats(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        try (FlatsDao flatsDao = DB_WORKER.createFlatsDao()) {
            req.setAttribute("select", req.getParameter("default") == null ? "Found Flats" : "All Flats");
            Flat flat = req.getParameter("default") == null ? parseFlat(req) : new Flat();
            SearchParameters searchParameters = (req.getParameter("default") == null) ?
                    parseSearchParameters(req) : new SearchParameters();
            setPreviousRequestParameters(req, flat, searchParameters);
            if (!validateSearchParameters(searchParameters)) {
                req.setAttribute("error_message", "Invalid input of search parameters, please try again");
                req.getRequestDispatcher("/WEB-INF/views/flats.jsp").forward(req, resp);
                return;
            }

            List<Flat> flats = req.getParameter("default") == null ?
                    flatsDao.selectFlats(searchParameters) : flatsDao.selectAllFlats();
            req.setAttribute("success_message", "Flats successfully found");
            req.setAttribute("flats", flats);
        } catch (SQLException e) {
            req.setAttribute("error_message", "Internal SQL error");
            System.err.println("Unable to get Flats");
            e.printStackTrace();
        }
        req.getRequestDispatcher("/WEB-INF/views/flats.jsp").forward(req, resp);
    }

    private Flat parseFlat(HttpServletRequest req) {

        Flat flat = null;

        try {
            District district = District.valueOf(req.getParameter("insert_district").toUpperCase());
            String address = req.getParameter("insert_address");
            int rooms = Integer.parseInt(req.getParameter("rooms"));
            int area = Integer.parseInt(req.getParameter("area"));
            int price = Integer.parseInt(req.getParameter("price"));
            flat = new Flat(district, address, rooms, area, price);
        } catch (IllegalArgumentException e) {
            System.err.println("Unable to parse request parameter(s)");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("One or more of parameters is empty");
            e.printStackTrace();
        }

        return flat;
    }

    private SearchParameters parseSearchParameters(HttpServletRequest req) {

        SearchParameters searchParameters = null;

        try {
            District district = District.safeParse(req.getParameter("select_district"));
            String address = req.getParameter("select_address");
            int fromRooms = Integer.parseInt(req.getParameter("from_rooms"));
            int toRooms = Integer.parseInt(req.getParameter("to_rooms"));
            int fromArea = Integer.parseInt(req.getParameter("from_area"));
            int toArea = Integer.parseInt(req.getParameter("to_area"));
            int fromPrice = Integer.parseInt(req.getParameter("from_price"));
            int toPrice = Integer.parseInt(req.getParameter("to_price"));
            searchParameters =  new SearchParameters(district, address, fromRooms, toRooms, fromArea, toArea, fromPrice, toPrice);
        } catch (IllegalArgumentException e) {
            System.err.println("Unable to parse request parameter(s)");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("One or more of parameters is empty");
            e.printStackTrace();
        }

        return searchParameters;
    }

    private boolean validateFlat(Flat flat) {
        if (flat == null || flat.getDistrict() == null || flat.getAddress() == null || flat.getAddress() == "" ||
                flat.getRooms() <= 0 || flat. getArea() < 20 || flat.getPrice() < 0)
            return false;
        return true;
    }

    private boolean validateSearchParameters(SearchParameters searchParameters) {
        if (searchParameters == null || searchParameters.getFromRooms() <= 0 || searchParameters.getToRooms() <= 0 ||
                searchParameters.getFromArea() < 0 || searchParameters.getToArea() <= 0 ||
                searchParameters.getFromPrice() < 0 || searchParameters.getToPrice() < 0)
            return false;
        return true;
    }

    private void setPreviousRequestParameters(HttpServletRequest req, Flat flat, SearchParameters searchParameters) {

        req.setAttribute("select_district", searchParameters.getDistrict());
        req.setAttribute("select_address", searchParameters.getAddress());
        req.setAttribute("from_rooms", searchParameters.getFromRooms());
        req.setAttribute("to_rooms", searchParameters.getToRooms());
        req.setAttribute("from_area", searchParameters.getFromArea());
        req.setAttribute("to_area", searchParameters.getToArea());
        req.setAttribute("from_price", searchParameters.getFromPrice());
        req.setAttribute("to_price", searchParameters.getToPrice());

        req.setAttribute("insert_district", flat.getDistrict());
        req.setAttribute("insert_address", flat.getAddress());
        req.setAttribute("rooms", flat.getRooms());
        req.setAttribute("area", flat.getArea());
        req.setAttribute("price", flat.getPrice());
    }
}
