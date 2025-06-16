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

@WebServlet("/editAdmin")
public class AdminEdit extends HttpServlet {
    private static final String SELECT_QUERY = "SELECT FULLNAME, PHONENUMBER, USERNAME, EMAIL FROM user_Admin WHERE USERNAME=?";
    private static final String UPDATE_QUERY = "UPDATE user_Admin SET FULLNAME=?, PHONENUMBER=?, EMAIL=? WHERE USERNAME=?";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Session Authentication Check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            showSweetAlert(pw, "Access Denied", "You must be logged in as an admin to edit details!", "error", "Userlogin.html");
            return;
        }

        String username = req.getParameter("id");
        if (username == null || username.trim().isEmpty()) {
            showSweetAlert(pw, "Error", "Invalid admin username.", "error", "adminList");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_QUERY)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pw.println("<html><head><script src='https://cdn.tailwindcss.com'></script><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script><title>Edit Admin</title></head><body>");
                pw.println("<div class='max-w-lg mx-auto bg-white shadow-lg rounded-lg p-6'>");
                pw.println("<h2 class='text-2xl font-bold text-center mb-4 text-gray-700'>Edit Admin Details</h2>");
                pw.println("<form method='POST'>");
                pw.println("<input type='hidden' name='username' value='" + username + "'>");
                pw.println("<label>Full Name:</label><input type='text' name='fullName' value='" + rs.getString(1) + "' class='border rounded w-full p-2' required>");
                pw.println("<label>Phone Number:</label><input type='text' name='phoneNumber' value='" + rs.getString(2) + "' class='border rounded w-full p-2' required>");
                pw.println("<label>Email:</label><input type='email' name='email' value='" + rs.getString(4) + "' class='border rounded w-full p-2' required>");
                pw.println("<button type='submit' class='bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600 w-full'>Update Admin</button>");
                pw.println("</form></div></body></html>");
            } else {
                showSweetAlert(pw, "Error", "Admin not found.", "error", "adminList");
            }
        } catch (Exception e) {
            showSweetAlert(pw, "Error", "Database error: " + e.getMessage(), "error", "adminList");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        String username = req.getParameter("username");
        String fullName = req.getParameter("fullName").trim();
        String phoneNumber = req.getParameter("phoneNumber").trim();
        String email = req.getParameter("email").trim();

        if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            showSweetAlert(pw, "Validation Error", "All fields must be filled!", "warning", "editAdmin?id=" + username);
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_QUERY)) {

            ps.setString(1, fullName);
            ps.setString(2, phoneNumber);
            ps.setString(3, email);
            ps.setString(4, username);

            int count = ps.executeUpdate();
            if (count == 1) {
                showSweetAlert(pw, "Success", "Admin Updated Successfully!", "success", "adminList");
            } else {
                showSweetAlert(pw, "Error", "Failed to update admin.", "error", "editAdmin?id=" + username);
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