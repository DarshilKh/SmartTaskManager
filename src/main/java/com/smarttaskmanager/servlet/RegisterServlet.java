package com.smarttaskmanager.servlet;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * RegisterServlet - Handles user registration
 * POST /register
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Darshil@18580";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate input
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            out.println("<h2>Error: All fields are required!</h2>");
            out.println("<a href='register.html'>Back to Registration</a>");
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Load MySQL JDBC Driver
            Class.forName(DB_DRIVER);
            
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // SQL Insert query
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            
            // Execute insert
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Registration successful - redirect to login
                response.sendRedirect("login.html");
            } else {
                out.println("<h2>Error: Registration failed!</h2>");
                out.println("<a href='register.html'>Back to Registration</a>");
            }
            
        } catch (ClassNotFoundException e) {
            out.println("<h2>Error: MySQL Driver not found!</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
            
        } catch (SQLException e) {
            // Check if email already exists
            if (e.getMessage().contains("Duplicate entry")) {
                out.println("<h2>Error: Email already registered!</h2>");
            } else {
                out.println("<h2>Error: Database error!</h2>");
                out.println("<p>" + e.getMessage() + "</p>");
            }
            e.printStackTrace();
            
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}