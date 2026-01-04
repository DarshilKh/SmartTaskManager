package com.smarttaskmanager.servlet;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;	
import javax.servlet.http.*;

@WebServlet("/addtask")
public class AddTaskServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Darshil@18580";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get userId from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        
        // Validate input
        if (title == null || title.trim().isEmpty()) {
            out.println("<h2>Error: Task title is required!</h2>");
            out.println("<a href='addtask.html'>Back to Add Task</a>");
            return;
        }
        
        if (description == null) {
            description = "";
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Insert new task with status = 'pending'
            String sql = "INSERT INTO tasks (user_id, title, description, status) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, "pending");
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.sendRedirect("viewtasks");
            } else {
                out.println("<h2>Error: Failed to add task!</h2>");
                out.println("<a href='addtask.html'>Back to Add Task</a>");
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
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}