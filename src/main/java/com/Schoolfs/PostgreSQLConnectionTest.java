package com.Schoolfs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/schoolfs"; // Update database name if needed
        String user = "postgres"; 
        String password = "1234"; // Replace with your actual password

        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

            // Establish connection
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
            System.out.println("✅ Connected to PostgreSQL Database Successfully!");

            // Close connection
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: PostgreSQL JDBC Driver not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to connect! " + e.getMessage());
        }
    }
}
