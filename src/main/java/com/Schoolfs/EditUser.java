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

@WebServlet("/editUser")
public class EditUser extends HttpServlet {
    private static final String SELECT_QUERY = "SELECT FIRSTNAME, LASTNAME, USERNAME, EMAIL, PHONENUMBER FROM user_table WHERE ID=?";
    private static final String UPDATE_QUERY = "UPDATE user_table SET FIRSTNAME=?, LASTNAME=?, USERNAME=?, EMAIL=?, PHONENUMBER=? WHERE ID=?";

    // ✅ Handles GET Requests (Displays User Edit Form)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Session Authentication Check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            showSweetAlert(pw, "Access Denied", "You must be logged in to edit users!", "error", "login.html");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            showSweetAlert(pw, "Error", "Missing or invalid user ID.", "error", "userList");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(SELECT_QUERY)) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    pw.println("<html><head><script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script><script src='https://cdn.tailwindcss.com'></script><title>Edit User</title></head><body>");
                    pw.println("<div class='max-w-lg mx-auto bg-white shadow-lg rounded-lg p-6'>");
                    pw.println("<h2 class='text-2xl font-bold text-center mb-4 text-gray-700'>Edit User</h2>");
                    pw.println("<form method='POST'>");
                    pw.println("<input type='hidden' name='id' value='" + id + "'>");
                    pw.println("<label>First Name:</label><input type='text' name='firstName' value='" + rs.getString(1) + "' class='border rounded w-full p-2' required>");
                    pw.println("<label>Last Name:</label><input type='text' name='lastName' value='" + rs.getString(2) + "' class='border rounded w-full p-2' required>");
                    pw.println("<label>Username:</label><input type='text' name='username' value='" + rs.getString(3) + "' class='border rounded w-full p-2' required>");
                    pw.println("<label>Email:</label><input type='email' name='email' value='" + rs.getString(4) + "' class='border rounded w-full p-2' required>");
                    pw.println("<label>Phone:</label><input type='text' name='phoneNumber' value='" + rs.getString(5) + "' class='border rounded w-full p-2' required>");
                    pw.println("<button type='submit' class='bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600 w-full'>Update User</button>");
                    pw.println("</form></div></body></html>");
                } else {
                    showSweetAlert(pw, "Error", "User not found.", "error", "userList");
                }
            }
        } catch (NumberFormatException e) {
            showSweetAlert(pw, "Error", "Invalid user ID format.", "error", "userList");
        } catch (Exception e) {
            showSweetAlert(pw, "Error", "Database error: " + e.getMessage(), "error", "userList");
        }
    }

    // ✅ Handles POST Requests (User Update Submission)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            showSweetAlert(pw, "Error", "Missing or invalid user ID.", "error", "userList");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            String firstName = req.getParameter("firstName").trim();
            String lastName = req.getParameter("lastName").trim();
            String username = req.getParameter("username").trim();
            String email = req.getParameter("email").trim();
            String phoneNumber = req.getParameter("phoneNumber").trim();

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                showSweetAlert(pw, "Validation Error", "All fields must be filled!", "warning", "editUser?id=" + id);
                return;
            }

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(UPDATE_QUERY)) {

                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, username);
                ps.setString(4, email);
                ps.setString(5, phoneNumber);
                ps.setInt(6, id);

                int count = ps.executeUpdate();
                if (count == 1) {
                    showSweetAlert(pw, "Success", "User Updated Successfully!", "success", "userList");
                } else {
                    showSweetAlert(pw, "Error", "Failed to update user.", "error", "editUser?id=" + id);
                }
            }
        } catch (Exception e) {
            showSweetAlert(pw, "Error", "Database error: " + e.getMessage(), "error", "userList");
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