Real-Time Code Editor

A real-time collaborative code editor that allows multiple users to edit, sync, and execute code with access control.

🚀 Features

Multi-User Collaboration: Users can edit shared files with controlled access.

Real-Time Syncing: Sync code updates with a click.

File Management: Create, list, and load files dynamically.

Code Execution: Run Java code directly from the editor.

User Authentication: Restricts editing access based on user roles.

Enhanced UI: Clean file list with tooltip support for long filenames.

🏗️ Tech Stack

Frontend: HTML, CSS, JavaScript, Monaco Editor

Backend: Java (Servlets)

Database: MySQL (for user authentication & file storage)

📜 Installation Guide

1️⃣ Clone the Repository

git clone https://github.com/your-username/repo-name.git
cd repo-name

2️⃣ Set Up MySQL Database

Create a database and tables for storing users and files.

Update database credentials in config.properties.

3️⃣ Deploy Backend on Tomcat

Compile the Java servlet files.

Deploy the WAR file in Apache Tomcat (webapps directory).

Start the Tomcat server.

4️⃣ Run the Frontend

Simply open login.html in a browser.

🛠️ Usage Guide

Login/Register to access the editor.

Create a File and set allowed users.

Select a File to start editing.

Click "Sync" to save changes.

Click "Run Code" to execute Java code.

Login

![Screenshot (246)](https://github.com/user-attachments/assets/7226e6ae-532d-43c4-9846-340a52644900)

HomePage

![Screenshot (247)](https://github.com/user-attachments/assets/72d0278a-2006-4f1d-a4f0-a36c6af2a126)

Selecting a File and Runs the code

![Screenshot (249)](https://github.com/user-attachments/assets/077e7964-16bb-443e-b1e1-d17485af1508)

Create a New File

![Screenshot (250)](https://github.com/user-attachments/assets/c6f48915-d88e-4917-b79b-73404df2eaee)

New File Added to the users who are all allowed to view or edit the file

![Screenshot (251)](https://github.com/user-attachments/assets/ec2b5c55-845d-463d-89b3-45944022748f)


📌 Future Improvements

Support for Multiple Languages (Python, JavaScript, etc.)

WebSocket-Based Live Editing

User Roles (Admin, Editor, Viewer)

👨‍💻 Contributors

Muthukumar

Happy coding! 🚀

