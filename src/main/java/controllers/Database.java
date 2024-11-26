package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public Connection databaseLink;

    public Connection getConnection() {
        String databasName ="";
        String databaseUser ="";
        String databasePassword ="";
        String url = "jdbc:mysql://localhost:3308/";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url , databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }
}
