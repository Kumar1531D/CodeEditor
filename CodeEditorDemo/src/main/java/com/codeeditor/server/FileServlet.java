package com.codeeditor.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/files")
public class FileServlet extends HttpServlet {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/codeeditor";
	private static final String JDBC_USER = "root";
	private static final String JDBC_PASS = "Kumar_1531.,";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		PrintWriter out = response.getWriter();
		JSONObject jsonResponse = new JSONObject();

		try {
			Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

			if (action.equals("get")) {

				String file_name = request.getParameter("fileName");
				String userName = request.getParameter("userName");

				PreparedStatement ps = con
						.prepareStatement("select content,last_edited_by,allowed_users from files where file_name=?");
				ps.setString(1, file_name);

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					String allowedUsers[] = rs.getString("allowed_users").split(",");

					if (allowedUsers != null && userName != null && check(allowedUsers,userName)) {
						jsonResponse.put("access", "ok");
						jsonResponse.put("content", rs.getString("content"));
						jsonResponse.put("lastEditedBy", rs.getString("last_edited_by"));
					} else {
						jsonResponse.put("access", "Notok");
					}
				}

				out.write(jsonResponse.toString());
				out.flush();

			} else if (action.equals("list")) {
				JSONArray jsonArray = new JSONArray();

				PreparedStatement ps = con.prepareStatement("select file_name,allowed_users from files");

				ResultSet rs = ps.executeQuery();
				
				String userName = request.getParameter("userName");
				System.out.println(userName);

				while (rs.next()) {
					String allowedUsers[] = rs.getString("allowed_users").split(",");
					if(check(allowedUsers,userName))
						jsonArray.put(rs.getString("file_name"));
				}

				out.write(jsonArray.toString());
				out.flush();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean check(String a[],String name) {
		for(int i=0;i<a.length;i++) {
			if(a[i].equals(name)) {
				return true;
			}
		}
		return false;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader reader = request.getReader();

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		JSONObject requestedData = new JSONObject(sb.toString());

		String userName = requestedData.getString("user");
		String fileName = requestedData.getString("name");
		String code = requestedData.getString("code");
		String action = request.getParameter("action");

		System.out.println("Inside doPost: " + userName + ", " + fileName + ", " + code);

		if (action.equals("update" )|| action==null) {
			try {
				Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
				String qry = "UPDATE files SET content = ?, last_edited_by = ? WHERE file_name = ?";

				PreparedStatement ps = con.prepareStatement(qry);
				ps.setString(1, code);
				ps.setString(2, userName);
				ps.setString(3, fileName);
				ps.executeUpdate();

				JSONObject responseData = new JSONObject();
				responseData.put("user", userName);
				responseData.put("message", "File updated successfully");

				response.setContentType("application/json");
				response.getWriter().write(responseData.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
				String qry = "insert into files values(null,?,?,?,?,?)";

				String allowedUsers = requestedData.getString("allowedUsers");
				PreparedStatement ps = con.prepareStatement(qry);
				ps.setString(1, fileName);
				ps.setString(2, code);
				ps.setString(3, userName);
				ps.setString(4, allowedUsers+","+userName);
				ps.setString(5, userName);
				ps.executeUpdate();

				JSONObject responseData = new JSONObject();
				responseData.put("user", userName);
				responseData.put("message", "File updated successfully");

				response.setContentType("application/json");
				response.getWriter().write(responseData.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		
//		String userName = requestedData.optString("user");
//		String fileName = requestedData.optString("name");
//		String code = requestedData.optString("code");
//		
//		System.out.println("Inside post : "+userName+fileName+code);
//		
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//		
//		try {
//			Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//			
//			String qry = "update files set content = ?, last_edited_by=? where file_name=?";
//			
//			PreparedStatement ps = con.prepareStatement(qry);
//			
//			ps.setString(1, code);
//			ps.setString(2, userName);
//			ps.setString(3, fileName);
//			
//			ps.executeUpdate();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		JSONObject responseData = new JSONObject();
//        responseData.put("user", userName);
//        responseData.put("message", "File updated successfully");
//
//        response.setContentType("application/json");
//        response.getWriter().write(responseData.toString());
//	}

}
