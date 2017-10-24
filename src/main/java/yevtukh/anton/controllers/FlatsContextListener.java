package yevtukh.anton.controllers;

import yevtukh.anton.database.DbWorker;
import yevtukh.anton.database.InitData;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anton on 14.10.2017.
 */
@WebListener
public class FlatsContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {
            DbWorker.getInstance().initDB();
        } catch (Exception e) {
            System.err.println("Failed to init DB");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        DbWorker.getInstance().stop();
    }
}
