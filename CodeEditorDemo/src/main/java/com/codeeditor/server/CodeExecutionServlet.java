package com.codeeditor.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/execute")
public class CodeExecutionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String code = request.getParameter("code");
        
        System.out.println(code);
        System.out.println("hr");

        String filePath = "Main.java";
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(code);
        } catch (IOException e) {
            e.printStackTrace();
            response.getWriter().write("File Error: " + e.getMessage());
        }

        // Compile the Java file
        Process compileProcess = Runtime.getRuntime().exec("javac Main.java");
        try {
            compileProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Execute the compiled Java program
        Process executeProcess = Runtime.getRuntime().exec("java Main");
        BufferedReader reader = new BufferedReader(new InputStreamReader(executeProcess.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        response.getWriter().write(output.toString());
    }
}	
