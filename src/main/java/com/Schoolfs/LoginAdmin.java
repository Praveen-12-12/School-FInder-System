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
import jakarta.servlet.http.HttpSession;

@WebServlet("/loginAdmin")
public class LoginAdmin extends HttpServlet {
    private static final String LOGIN_QUERY = "SELECT PASSWORD FROM user_Admin WHERE USERNAME=?";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Safely Retrieve Parameters
        String username = safeTrim(req.getParameter("username"));
        String password = safeTrim(req.getParameter("password"));

        // ✅ Debugging: Print received values
        System.out.println("Received Username: " + username);
        System.out.println("Received Password: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            showSweetAlert(pw, "Missing Fields", "Please enter both username and password.", "warning", "Userlogin.html");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(LOGIN_QUERY)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString(1);

                // ✅ Ensure hash uses correct salt format (`$2a$`)
                if (storedHash.startsWith("$2a$") && BCrypt.checkpw(password, storedHash)) {
                    // ✅ Create Session for Logged-in User
                    HttpSession session = req.getSession();
                    session.setAttribute("username", username);

                    // ✅ Redirect with proper path mapping
                    showSweetAlert(pw, "Login Successful", "Welcome back, " + username + "!", "success", "dashboard.html");
                } else {
                    showSweetAlert(pw, "Invalid Login", "Incorrect username or password.", "error", "Userlogin.html");
                }
            } else {
                showSweetAlert(pw, "Invalid Login", "Username not found.", "error", "Userlogin.html");
            }
        } catch (Exception e) {
            showSweetAlert(pw, "Server Error", "Login failed: " + e.getMessage(), "error", "Userlogin.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("Userlogin.html"); // Redirect users to registration page if they try GET request
    }

    private void showSweetAlert(PrintWriter pw, String title, String message, String icon, String redirect) {
        pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script></head><body>");
        pw.println("<script>");
        pw.println("Swal.fire({title: '" + title + "', text: '" + message + "', icon: '" + icon + "', confirmButtonText: 'OK'})");
        pw.println(".then(() => {");
        pw.println("   setTimeout(() => { window.location.href = '" + redirect + "'; }, 500);"); // ✅ Added slight delay for smoother execution
        pw.println("});");
        pw.println("</script></body></html>");
    }

    private String safeTrim(String value) {
        return value != null ? value.trim() : "";
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
    }
}