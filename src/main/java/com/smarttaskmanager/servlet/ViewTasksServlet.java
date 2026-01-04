package com.smarttaskmanager.servlet;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/viewtasks")
public class ViewTasksServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Darshil@18580";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT id, title, description, status, created_at FROM tasks WHERE user_id = ? ORDER BY created_at DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Smart Task Manager - Dashboard</title>");
            out.println("<style>");
            out.println("body { font-family: 'Poppins', sans-serif; margin: 0; background: linear-gradient(135deg, #74ABE2, #5563DE); color: #333; }");
            out.println(".navbar { background-color: rgba(255,255,255,0.2); backdrop-filter: blur(10px); padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; color: white; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
            out.println(".navbar h1 { margin: 0; font-size: 26px; }");
            out.println(".user { font-weight: 500; }");
            out.println(".container { width: 90%; max-width: 1000px; margin: 40px auto; background: white; border-radius: 15px; padding: 30px; box-shadow: 0 10px 25px rgba(0,0,0,0.2); animation: fadeIn 0.6s ease-in-out; }");
            out.println("table { width: 100%; border-collapse: collapse; }");
            out.println("th, td { padding: 14px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background: #5563DE; color: white; }");
            out.println("tr:hover { background-color: #f3f5ff; transition: 0.3s; }");
            out.println(".actions { margin-top: 20px; text-align: center; }");
            out.println(".btn { padding: 10px 20px; border: none; border-radius: 8px; color: white; cursor: pointer; font-size: 15px; transition: 0.3s; }");
            out.println(".btn-add { background: #5563DE; }");
            out.println(".btn-add:hover { background: #4052c4; }");
            out.println(".btn-logout { background: #6c757d; margin-left: 10px; }");
            out.println(".btn-logout:hover { background: #5a6268; }");
            out.println(".btn-delete { background: #dc3545; }");
            out.println(".btn-delete:hover { background: #b02a37; }");
            out.println(".status-pending { color: #ff9800; font-weight: bold; }");
            out.println(".status-done { color: #28a745; font-weight: bold; }");
            out.println(".no-tasks { text-align: center; padding: 50px; color: #666; }");
            out.println("@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div class='navbar'>");
            out.println("<h1>Smart Task Manager</h1>");
            out.println("<div class='user'>Welcome, " + userName + " 👋</div>");
            out.println("</div>");

            out.println("<div class='container'>");
            out.println("<h2 style='text-align:center; color:#5563DE;'>Your Task Dashboard</h2>");
            out.println("<hr style='border:1px solid #eee;'>");

            if (!rs.isBeforeFirst()) {
                out.println("<div class='no-tasks'>");
                out.println("<h3>No tasks yet 🗒️</h3>");
                out.println("<p>Click below to add your first task.</p>");
                out.println("</div>");
            } else {
                out.println("<table>");
                out.println("<tr><th>ID</th><th>Title</th><th>Description</th><th>Status</th><th>Created</th><th>Actions</th></tr>");
                while (rs.next()) {
                    int taskId = rs.getInt("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("created_at");

                    String statusClass = status.equalsIgnoreCase("done") ? "status-done" : "status-pending";

                    out.println("<tr>");
                    out.println("<td>" + taskId + "</td>");
                    out.println("<td>" + escapeHtml(title) + "</td>");
                    out.println("<td>" + escapeHtml(description) + "</td>");
                    out.println("<td class='" + statusClass + "'>" + status + "</td>");
                    out.println("<td>" + createdAt + "</td>");
                    out.println("<td>");
                    out.println("<form method='GET' action='deletetask' style='display:inline;'>");
                    out.println("<input type='hidden' name='id' value='" + taskId + "'>");
                    out.println("<button class='btn btn-delete'>Delete</button>");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }

            out.println("<div class='actions'>");
            out.println("<a href='addtask.html'><button class='btn btn-add'>➕ Add New Task</button></a>");
            out.println("<a href='logout'><button class='btn btn-logout'>🚪 Logout</button></a>");
            out.println("</div>");
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                   .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
