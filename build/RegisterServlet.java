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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String CHECK_USERNAME_QUERY = "SELECT COUNT(*) FROM user_table WHERE USERNAME=?";
    private static final String INSERT_QUERY = 
        "INSERT INTO user_table (FIRSTNAME, LASTNAME, USERNAME, PASSWORD, EMAIL, PHONENUMBER, DATE_AND_TIME) " +
        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        String firstName = safeTrim(req.getParameter("firstName"));
        String lastName = safeTrim(req.getParameter("lastName"));
        String username = safeTrim(req.getParameter("username"));
        String password = safeTrim(req.getParameter("password"));
        String email = safeTrim(req.getParameter("email"));
        String phoneNumber = safeTrim(req.getParameter("phoneNumber"));

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            showSweetAlert(pw, "Missing Fields", "Please fill out all required fields.", "warning");
            return;
        }

        if (!phoneNumber.matches("\\d{10}")) {
            showSweetAlert(pw, "Invalid Phone", "Phone number must be **10 digits**.", "error");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement checkUser = con.prepareStatement(CHECK_USERNAME_QUERY)) {

            // Check if username already exists
            checkUser.setString(1, username);
            ResultSet rs = checkUser.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showSweetAlert(pw, "Username Exists", "This username is already taken!", "warning");
                return;
            }

            // Secure Password Hashing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            try (PreparedStatement ps = con.prepareStatement(INSERT_QUERY)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, username);
                ps.setString(4, hashedPassword);
                ps.setString(5, email);
                ps.setString(6, phoneNumber);
                ps.executeUpdate();

                showSweetAlert(pw, "Registration Successful", "Welcome, " + username + "!", "success");
            }

        } catch (Exception e) {
            showSweetAlert(pw, "Server Error", "Something went wrong: " + e.getMessage(), "error");
        }
    }

    private void showSweetAlert(PrintWriter pw, String title, String message, String icon) {
        pw.println("<html><head>");
        pw.println("<script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script>");
        pw.println("</head><body>");
        pw.println("<script>Swal.fire({title: '" + escapeJS(title) + "', text: '" + escapeJS(message) + "', icon: '" + icon + "', confirmButtonText: 'OK'});</script>");
        pw.println("</body></html>");
    }

    private String escapeJS(String text) {
        return text.replace("'", "\\'");
    }

    private String safeTrim(String value) {
        return value != null ? value.trim() : "";
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("register.html"); // Redirect users to the registration form
    }
}