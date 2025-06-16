package com.Schoolfs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/deleteAdmin")
public class AdminDelete extends HttpServlet {
    private static final String DELETE_QUERY = "DELETE FROM user_Admin WHERE USERNAME=?";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Session Authentication Check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            showSweetAlert(pw, "Access Denied", "You must be logged in as an admin to perform this action!", "error", "Userlogin.html");
            return;
        }

        String username = req.getParameter("id");
        if (username == null || username.trim().isEmpty()) {
            showSweetAlert(pw, "Error", "Invalid admin username.", "error", "adminList");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_QUERY)) {

            ps.setString(1, username);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                showSweetAlert(pw, "Success", "Admin deleted successfully!", "success", "adminList");
            } else {
                showSweetAlert(pw, "Error", "Failed to delete admin. Admin not found.", "error", "adminList");
            }
        } catch (Exception e) {
            showSweetAlert(pw, "Error", "Database error: " + e.getMessage(), "error", "adminList");
        }
    }

    private void showSweetAlert(PrintWriter pw, String title, String message, String icon, String redirect) {
        pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script></head><body>");
        pw.println("<script>Swal.fire({title: '" + title + "', text: '" + message + "', icon: '" + icon + "', confirmButtonText: 'OK'})");
        pw.println(".then(() => {window.location.href = '" + redirect + "';});");
        pw.println("</script></body></html>");
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
    }
}