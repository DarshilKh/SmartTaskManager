package com.smarttaskmanager.servlet;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Darshil@18580";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            out.println("<h2>Error: Email and password are required!</h2>");
            out.println("<a href='login.html'>Back to Login</a>");
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Query to find user by email and password
            String sql = "SELECT id, name FROM users WHERE email = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();
            
            // Check if user found
            if (rs.next()) {
                // Get user ID and name
                int userId = rs.getInt("id");
                String userName = rs.getString("name");
                
                // Create session and store userId
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", userId);
                session.setAttribute("userName", userName);
                
                // Redirect to viewtasks
                response.sendRedirect("viewtasks");
            } else {
                out.println("<h2>Invalid Credentials</h2>");
                out.println("<p>Email or password is incorrect.</p>");
                out.println("<a href='login.html'>Back to Login</a>");
            }
            
        } catch (ClassNotFoundException e) {
            out.println("<h2>Error: MySQL Driver not found!</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
            
        } catch (SQLException e) {
            out.println("<h2>Error: Database error!</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
