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

@WebServlet("/userList")
public class Userlist extends HttpServlet {
    private static final String QUERY = "SELECT ID, FIRSTNAME, LASTNAME, USERNAME, EMAIL, PHONENUMBER, DATE_AND_TIME FROM user_table";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Session Authentication Check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            showSweetAlert(pw, "Access Denied", "You must be logged in to view this page!", "error", "register.html");
            return;
        }

        pw.println("<html><head>");
        pw.println("<script src='https://cdn.tailwindcss.com'></script>");
        pw.println("<script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script>");
        pw.println("<title>Admin Panel</title></head>");
        pw.println("<body class='bg-gray-100 flex flex-col items-center justify-center min-h-screen p-6'>");

        pw.println("<div class='max-w-7xl w-full bg-white shadow-lg rounded-lg p-6 transition duration-500 ease-in-out transform hover:scale-105'>");
        pw.println("<h2 class='text-3xl font-bold text-center mb-6 text-gray-700 animate-fade-in'>Admin Panel</h2>");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(QUERY);
             ResultSet rs = ps.executeQuery()) {

            pw.println("<div class='overflow-x-auto'><table class='min-w-full bg-white border border-gray-300 rounded-lg shadow-sm'>");
            pw.println("<thead class='bg-blue-500 text-white'><tr><th class='p-3'>User ID</th><th class='p-3'>Name</th><th class='p-3'>Username</th><th class='p-3'>Email</th><th class='p-3'>Phone</th><th class='p-3'>Actions</th></tr></thead><tbody>");

            while (rs.next()) {
                pw.println("<tr class='hover:bg-gray-100 transition duration-200'>");
                pw.println("<td class='p-3'>" + rs.getInt(1) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(2) + " " + rs.getString(3) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(4) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(5) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(6) + "</td>");
                pw.println("<td class='p-3'>");
                pw.println("<button class='bg-yellow-500 text-white py-1 px-3 rounded hover:bg-yellow-600' onclick='confirmEdit(\"editUser?id=" + rs.getInt(1) + "\", \"Edit User?\")'>Edit</button>");
                pw.println("<button class='bg-red-500 text-white py-1 px-3 rounded hover:bg-red-600' onclick='confirmDelete(\"deleteUser?id=" + rs.getInt(1) + "\", \"Delete User?\")'>Delete</button>");
                pw.println("</td></tr>");
            }
            pw.println("</tbody></table></div>");
        } catch (Exception e) {
            showSweetAlert(pw, "Database Error", "Something went wrong, please try again!", "error", "userList");
        }

        // Smooth Popup Animations
        pw.println("<script>");
        pw.println("function confirmEdit(url, message) {");
        pw.println("Swal.fire({icon: 'info', title: message, showCancelButton: true, confirmButtonText: 'Edit', cancelButtonText: 'Cancel'}).then((result) => {");
        pw.println("if (result.isConfirmed) { window.location.href = url; }});");
        pw.println("}");

        pw.println("function confirmDelete(url, message) {");
        pw.println("Swal.fire({icon: 'warning', title: message, showCancelButton: true, confirmButtonText: 'Delete', cancelButtonText: 'Cancel'}).then((result) => {");
        pw.println("if (result.isConfirmed) { window.location.href = url; }});");
        pw.println("}");
        pw.println("</script>");

        pw.println("</div></body></html>");
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