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

@WebServlet("/adminList")
public class Adminlist extends HttpServlet {
    private static final String QUERY = "SELECT FULLNAME, PHONENUMBER, USERNAME, EMAIL, DATE_AND_TIME FROM user_Admin";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        // ✅ Authentication Check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            showSweetAlert(pw, "Access Denied", "You must be logged in as an admin to view this page!", "error", "register.html");
            return;
        }

        pw.println("<!DOCTYPE html><html lang='en'>");
        pw.println("<head>");
        pw.println("<script src='https://cdn.tailwindcss.com'></script>");
        pw.println("<script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script>");
        pw.println("<title>Admin List | School Finder</title>");
        pw.println("<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600&display=swap' rel='stylesheet'>"); // Iconic Font
        pw.println("<style>");
        pw.println("body { font-family: 'Inter', sans-serif; background-color: #e7eaf6; }");
        pw.println(".logo { font-family: 'Poppins', sans-serif; font-size: 24px; font-weight: 600; color: white; }"); // Iconic Font Styling
        pw.println("</style>");
        pw.println("</head><body>");
       
        pw.println("<style>");
        pw.println("body { margin: 0; padding: 0;  font-family: Arial, sans-serif; }");
        pw.println(".navbar { position: fixed; top: 20px; left: 50%; transform: translateX(-50%); background: #38598b;");
        pw.println("backdrop-filter: blur(10px); padding: 10px 20px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3); transition: 0.3s ease-in-out;");
        pw.println("width: 95%; display: flex; align-items: center; justify-content: space-between; }");
        pw.println(".navbar .brand { font-size: 20px; font-weight: bold; color: #fff; text-decoration: none; padding-left: 20px; }");
        pw.println(".navbar .static { font-size: 20px; font-weight: bold; color: #fff; text-decoration: none; justify-content: center; padding-left: 50px; }");
        pw.println(".navbar ul { list-style: none; padding: 0; display: flex; gap: 15px; justify-content: right; }");
        pw.println(".navbar ul li { display: inline-block; }");
        pw.println(".navbar ul li a { text-decoration: none; color: #fff; font-weight: bold; padding: 8px 16px; transition: 0.3s ease-in-out; }");
        pw.println(".navbar ul li a:hover { background: #141f31; border-radius: 2px; }");
        pw.println("</style>");
        pw.println("</head><body>");

        pw.println("<nav class='navbar'>");
        pw.println("<span class='brand'>School Finder System</span>");
        pw.println("<span class='static'>Welcome to Admin Dashboard</span>");
        pw.println("<ul>");
        pw.println("<li><a href='index.html'>Home</a></li>");
        pw.println("<li><a href='register.html'>Logout</a></li>");
       
        pw.println("</ul>");
        pw.println("</nav>");

  

        // ✅ Main Section
        pw.println("<main class='mt-20 text-center p-6'><h1 class='text-4xl font-bold text-[#38598b] logo'>Welcome to School Finder</h1>");
        pw.println("<p class='mt-2 text-[#a2a8d3]'>Find the best schools near you with ease!</p></main>");

        // ✅ User List Table
        pw.println("<div class='max-w-7xl mx-auto bg-white p-6 rounded-lg shadow-md mt-6'>");
        pw.println("<h2 class='text-3xl font-semibold text-[#38598b] mb-4 text-center'>Registered Users</h2>");
        pw.println("<div class='overflow-x-auto'><table class='w-full border border-gray-300 rounded-lg shadow-sm text-center'>");
        pw.println("<thead class='bg-[#38598b] text-white'><tr><th class='p-3'>Full Name</th><th class='p-3'>Phone</th><th class='p-3'>Username</th><th class='p-3'>Email</th><th class='p-3'>Date & Time</th><th class='p-3'>Actions</th></tr></thead><tbody>");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(QUERY);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pw.println("<tr class='border-b hover:bg-[#e7eaf6]'>");
                pw.println("<td class='p-3'>" + rs.getString(1) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(2) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(3) + "</td>");
                pw.println("<td class='p-3'>" + rs.getString(4) + "</td>");
                pw.println("<td class='p-3'>" + rs.getTimestamp(5) + "</td>");
                pw.println("<td class='p-3'>");
                pw.println("<button class='bg-yellow-500 text-white py-1 px-3 rounded hover:bg-yellow-600' onclick='confirmEdit(\"editAdmin?id=" + rs.getString(3) + "\", \"Edit Admin?\")'>Edit</button>");
                pw.println("<button class='bg-red-500 text-white py-1 px-3 rounded hover:bg-red-600 ml-2' onclick='confirmDelete(\"deleteAdmin?id=" + rs.getString(3) + "\", \"Delete Admin?\")'>Delete</button>");
                pw.println("</td></tr>");
            }
            pw.println("</tbody></table></div></div>");
        } catch (Exception e) {
            showSweetAlert(pw, "Database Error", "Something went wrong, please try again!", "error", "adminList");
        }

        // ✅ SweetAlert Script
        pw.println("<script>");
        pw.println("function confirmEdit(url, message) { Swal.fire({icon: 'info', title: message, showCancelButton: true, confirmButtonText: 'Edit', cancelButtonText: 'Cancel'}).then((result) => { if (result.isConfirmed) { window.location.href = url; }}); }");
        pw.println("function confirmDelete(url, message) { Swal.fire({icon: 'warning', title: message, showCancelButton: true, confirmButtonText: 'Delete', cancelButtonText: 'Cancel'}).then((result) => { if (result.isConfirmed) { window.location.href = url; }}); }");
        pw.println("</script>");

        pw.println("</body></html>");
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