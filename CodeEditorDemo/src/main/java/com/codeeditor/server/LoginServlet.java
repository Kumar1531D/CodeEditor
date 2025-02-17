package com.codeeditor.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/codeeditor";
    private static final String JDBC_USER = "root"; 
    private static final String JDBC_PASS = "Kumar_1531.,";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		String uname = request.getParameter("username");
		String password = request.getParameter("password");
		
		JSONObject jsonResponse = new JSONObject();
		
		if(checkInfo(uname, password)) {
			jsonResponse.put("access", "ok");
		}
		else {
			jsonResponse.put("access", "notok");
		}
		
		out.write(jsonResponse.toString());
		out.flush();
		
	}
	
	public boolean checkInfo(String uname,String pass) { 
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
			
			PreparedStatement ps = con.prepareStatement("select * from loginDetails where user_name=? and password=?");
			
			ps.setString(1, uname);
			ps.setString(2, pass);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				return true;
			}
			
			con.close();
			ps.close();
			rs.close();
			
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
		
	}

}
