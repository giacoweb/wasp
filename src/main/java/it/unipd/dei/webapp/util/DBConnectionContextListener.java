package it.unipd.dei.webapp.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnectionContextListener implements ServletContextListener {

    private static final String JNDI_NAME = "java:comp/env/jdbc/fintrack";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup(JNDI_NAME);
            DBConnection.setDataSource(dataSource);
        } catch (NamingException e) {
            throw new IllegalStateException("Failed to look up JNDI DataSource " + JNDI_NAME, e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.setDataSource(null);
    }
}
