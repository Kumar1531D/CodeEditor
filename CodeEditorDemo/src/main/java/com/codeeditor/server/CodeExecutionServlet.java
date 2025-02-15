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

        System.out.println("Received code: \n" + code);

        // Ensure directory exists
        String path = "M:/newfolder";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();  // Create directory if it doesn't exist
        }

        File javaFile = new File(directory, "Main.java");

        // Write Java code to file
        try (FileWriter fileWriter = new FileWriter(javaFile)) {
            fileWriter.write(code);
        } catch (IOException e) {
            response.getWriter().write("File Error: " + e.getMessage());
            return;
        }

        // Compile Java file
        ProcessBuilder compileProcess = new ProcessBuilder("javac", javaFile.getAbsolutePath());
        compileProcess.directory(directory);  // Set working directory
        compileProcess.redirectErrorStream(true);
        Process compile = compileProcess.start();

        // Capture compilation errors
        BufferedReader compileReader = new BufferedReader(new InputStreamReader(compile.getInputStream()));
        StringBuilder compileErrors = new StringBuilder();
        String line;
        while ((line = compileReader.readLine()) != null) {
            compileErrors.append(line).append("\n");
        }

        try {
            int compileExitCode = compile.waitFor();
            if (compileExitCode != 0) {
                response.getWriter().write("Compilation Error:\n" + compileErrors.toString());
                return;
            }
        } catch (InterruptedException e) {
            response.getWriter().write("Compilation Interrupted: " + e.getMessage());
            return;
        }

        // Verify if Main.class was created
        File classFile = new File(directory, "Main.class");
        if (!classFile.exists()) {
            response.getWriter().write("Error: Main.class not found after compilation.");
            return;
        }

        // Run Java program with correct classpath
        ProcessBuilder runProcess = new ProcessBuilder("java", "-cp", directory.getAbsolutePath(), "Main");
        runProcess.directory(directory);  // Ensure correct working directory
        runProcess.redirectErrorStream(true);
        Process run = runProcess.start();

        // Capture output
        BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Send response
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(output.toString());
    }
}
