package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/persondb";
    private static final String USER = "root";  //  nom d'utilisateur MySQL
    private static final String PASSWORD = "Mysql";  //  mot de passe MySQL

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
