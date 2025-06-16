package com.Schoolfs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/registerAdmin")
public class RegisterAdmin extends HttpServlet {
    private static final String INSERT_QUERY =
        "INSERT INTO user_Admin (FULLNAME, USERNAME, PASSWORD, EMAIL, PHONENUMBER, DATE_AND_TIME) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Safely Retrieve Parameters
        String fullName = safeTrim(req.getParameter("fullName"));
        String username = safeTrim(req.getParameter("username"));
        String password = safeTrim(req.getParameter("password"));
        String email = safeTrim(req.getParameter("email"));
        String phoneNumber = safeTrim(req.getParameter("phoneNumber"));

        // ✅ Debugging: Print received values
        System.out.println("Full Name: " + fullName);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Password: " + password);

        // ✅ Input Validation
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            showSweetAlert(pw, "Validation Error", "All fields must be filled!", "warning", "Userlogin.html");
            return;
        }
        if (!phoneNumber.matches("\\d{10}")) {
            showSweetAlert(pw, "Error", "Phone number must be exactly 10 digits.", "error", "Userlogin.html");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_QUERY)) {

            // ✅ Secure Password Hashing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            System.out.println("Hashed Password: " + hashedPassword); // Debugging

            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, hashedPassword);
            ps.setString(4, email);
            ps.setString(5, phoneNumber);

            int count = ps.executeUpdate();
            if (count == 1) {
                showSweetAlert(pw, "Registration Successful", "Admin registered successfully!", "success", "Userlogin.html");
            } else {
                showSweetAlert(pw, "Registration Failed", "Something went wrong. Try again!", "error", "Userlogin.html");
            }

        } catch (Exception e) {
            showSweetAlert(pw, "Server Error", "Database error: " + e.getMessage(), "error", "Userlogin.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("Userlogin.html"); // Redirect users to the login page
    }

    private void showSweetAlert(PrintWriter pw, String title, String message, String icon, String redirect) {
        pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script></head><body>");
        pw.println("<script>Swal.fire({title: '" + title + "', text: '" + message + "', icon: '" + icon + "', confirmButtonText: 'OK'})");
        pw.println(".then(() => {window.location.href = '" + redirect + "';});");
        pw.println("</script></body></html>");
    }

    private String safeTrim(String value) {
        return value != null ? value.trim() : "";
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        System.out.println("Database Connection Established Successfully!"); // Debugging
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
    }
}