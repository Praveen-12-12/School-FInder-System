package com.Schoolfs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/deleteUser")
public class DeleteUser extends HttpServlet {
    private static final String DELETE_QUERY = "DELETE FROM user_table WHERE ID=?";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            showSweetAlert(pw, "Error", "Missing or invalid user ID.", "error", "userList");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Class.forName("org.postgresql.Driver");

            try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/schoolfs", "postgres", "1234");
                 PreparedStatement ps = con.prepareStatement(DELETE_QUERY)) {

                ps.setInt(1, id);
                int count = ps.executeUpdate();

                if (count == 1) {
                    showSweetAlert(pw, "Success", "User deleted successfully!", "success", "userList");
                } else {
                    showSweetAlert(pw, "Error", "User not found or already deleted.", "error", "userList");
                }
            }
        } catch (NumberFormatException e) {
            showSweetAlert(pw, "Error", "Invalid ID format.", "error", "userList");
        } catch (Exception e) {
            showSweetAlert(pw, "Server Error", "Deletion failed: " + e.getMessage(), "error", "userList");
        }
    }

    private void showSweetAlert(PrintWriter pw, String title, String message, String icon, String redirect) {
        pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script></head><body>");
        pw.println("<script>Swal.fire({title: '" + title + "', text: '" + message + "', icon: '" + icon + "', confirmButtonText: 'OK'}).then(() => {window.location.href = '" + redirect + "';});</script>");
        pw.println("</body></html>");
    }
}