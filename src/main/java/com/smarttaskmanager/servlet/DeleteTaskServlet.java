package com.smarttaskmanager.servlet;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/deletetask")
public class DeleteTaskServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Darshil@18580";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        // Get task ID from URL parameter
        String taskIdParam = request.getParameter("id");
        if (taskIdParam == null || taskIdParam.trim().isEmpty()) {
            response.sendRedirect("viewtasks");
            return;
        }
        
        try {
            int taskId = Integer.parseInt(taskIdParam);
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                Class.forName(DB_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                
                // Delete the task
                String sql = "DELETE FROM tasks WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, taskId);
                
                pstmt.executeUpdate();
                
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        // Redirect back to viewtasks
        response.sendRedirect("viewtasks");
    }
}
