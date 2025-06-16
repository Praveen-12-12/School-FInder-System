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
    private static final String CHECK_EXISTING_USER_QUERY = 
        "SELECT COUNT(*) FROM user_table WHERE USERNAME=? OR EMAIL=? OR PHONENUMBER=?";
    
    private static final String INSERT_QUERY =
        "INSERT INTO user_table (FULLNAME, USERNAME, PASSWORD, EMAIL, PHONENUMBER, DATE_AND_TIME) " + 
        "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Retrieve and sanitize input
        String fullName = safeTrim(req.getParameter("fullname"));
        String username = safeTrim(req.getParameter("username"));
        String password = safeTrim(req.getParameter("password"));
        String email = safeTrim(req.getParameter("email"));
        String phoneNumber = safeTrim(req.getParameter("phoneNumber"));

        // ✅ Debugging logs
        System.out.println("Debug - Full Name: " + fullName);
        System.out.println("Debug - Username: " + username);
        System.out.println("Debug - Email: " + email);
        System.out.println("Debug - Phone Number: " + phoneNumber);
        System.out.println("Debug - Password: " + password);

        // ✅ Input Validation
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            showSweetAlert(pw, "Validation Error", "All fields must be filled!", "warning");
            return;
        }
        if (!phoneNumber.matches("\\d{10}")) {
            showSweetAlert(pw, "Error", "Phone number must be exactly 10 digits.", "error");
            return;
        }
        if (!email.matches("^[\\w.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            showSweetAlert(pw, "Error", "Invalid email format!", "error");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement checkUser = con.prepareStatement(CHECK_EXISTING_USER_QUERY)) {

            // ✅ Check if username, email, or phone number already exists
            checkUser.setString(1, username);
            checkUser.setString(2, email);
            checkUser.setString(3, phoneNumber);
            ResultSet rs = checkUser.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showSweetAlert(pw, "Duplicate Entry", "Username, email, or phone number already exists!", "warning");
                return;
            }

            // ✅ Secure Password Hashing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            try (PreparedStatement ps = con.prepareStatement(INSERT_QUERY)) {
                ps.setString(1, fullName);
                ps.setString(2, username);
                ps.setString(3, hashedPassword);
                ps.setString(4, email);
                ps.setString(5, phoneNumber);

                int count = ps.executeUpdate();
                if (count == 1) {
                    showSweetAlertRedirect(pw, "Registration Successful", "Welcome, " + username + "!", "success", "register.html");
                } else {
                    showSweetAlert(pw, "Registration Failed", "Something went wrong. Try again!", "error");
                }
            }
        } catch (Exception e) {
            showSweetAlert(pw, "Server Error", "Database error: " + e.getMessage(), "error");
        }
    }

    private void showSweetAlert(PrintWriter pw, String title, String message, String icon) {
        pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script></head><body>");
        pw.println("<script>Swal.fire({title: '" + escapeJS(title) + "', text: '" + escapeJS(message) + "', icon: '" + icon + "', confirmButtonText: 'OK'})");
        pw.println(".then(() => {window.history.back();});");
        pw.println("</script></body></html>");
    }

    private void showSweetAlertRedirect(PrintWriter pw, String title, String message, String icon, String redirect) {
        pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script></head><body>");
        pw.println("<script>Swal.fire({title: '" + escapeJS(title) + "', text: '" + escapeJS(message) + "', icon: '" + icon + "', confirmButtonText: 'OK'})");
        pw.println(".then(() => {window.location.href = '" + redirect + "';});");
        pw.println("</script></body></html>");
    }

    private String escapeJS(String text) {
        return text.replace("'", "\\'");
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
    }

    private String safeTrim(String value) {
        return value != null ? value.trim() : "";
    }
}