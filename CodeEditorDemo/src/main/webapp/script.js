let editor;
let username = sessionStorage.getItem("username");
let currentFile = null;
let lastSyncedContent = null;

require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.34.0/min/vs' } });

require(['vs/editor/editor.main'], function() {
	editor = monaco.editor.create(document.getElementById('editor-container'), {
		value: "// Select a file to start editing...",
		language: 'java',
		theme: 'vs-dark',
		automaticLayout: true
	});

	loadFiles();

	document.getElementById("sync-button").addEventListener("click", function() {
		if (currentFile) {
			saveFile(currentFile, editor.getValue());
		}
		else {
			alert("Select a file first!");
		}
	});

	document.getElementById("run-button").addEventListener("click", function() {
		editor.trigger('keyboard', 'type', ''); // Force Monaco to register changes
		let code = editor.getValue();

		console.log("Sending Code to Server:\n" + code);

		fetch("/CodeEditorDemo/execute", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: "code=" + encodeURIComponent(code) + "&timestamp=" + new Date().getTime() // Prevent caching
		})
			.then(response => {
				if (!response.ok) {
					throw new Error("Server response was not OK");
				}
				return response.text();
			})
			.then(output => {
				console.log("Output from Server:\n" + output);
				document.getElementById("output").innerText = output ?? "✅ Code ran successfully!";
			})
			.catch(error => document.getElementById("output").innerText = `❌ Error: ${error.message}`);
	});


	setInterval(checkForUpdates, 5000);

});

function loadFiles() {

	let fileListContainer = document.querySelector(".container");
	let fileList = document.getElementById("file-list");

	// Ensure the "Create File" button is added only once
	if (!document.querySelector(".create-file-btn")) {
		let createFileButton = document.createElement("button");
		createFileButton.innerText = "➕ Create a File";
		createFileButton.classList.add("create-file-btn");
		createFileButton.onclick = createNewFile;

		fileListContainer.insertBefore(createFileButton, fileListContainer.firstChild);
	}

	// Clear only the existing

	fetch(`/CodeEditorDemo/files?action=list&userName=${username}`)
		.then(response => response.json())
		.then(files => {
			let fileList = document.getElementById('file-list');
			fileList.innerHTML = "";
			files.forEach(file => {
				let li = document.createElement("li");
				li.textContent = file;
				li.setAttribute("data-filename", file);
				li.setAttribute("title", "");  // Disable default tooltip

				// Create custom tooltip
				let tooltip = document.createElement("span");
				tooltip.classList.add("tooltip");
				tooltip.textContent = file;
				li.appendChild(tooltip);
				li.onclick = () => {
					loadFile(file);

					document.querySelectorAll("#file-list li").forEach(el => el.classList.remove("selected-file"));
					li.classList.add("selected-file");
				};
				fileList.appendChild(li);
			});
		})
		.catch(error => console.error("Error loading files:", error));
}

function createNewFile() {
	let fileName = prompt("Enter new file name:");
	if (!fileName) return;

	let allowed_users = prompt("Enter the allowed User's Username (comma separated)");
	if (!allowed_users) return;

	fetch('/CodeEditorDemo/files?action=create', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ name: fileName, user: username, code: "", allowedUsers: allowed_users }) // Empty content for a new file
	})
		.then(response => response.json())
		.then(data => {
			alert(`✅ File '${fileName}' created successfully!`);
			loadFiles(); // Refresh file list
		})
		.catch(error => console.error("Error creating file:", error));
}


function loadFile(fileName) {

	if (!username) {
		alert("❌ Error: User is not logged in.");
		return;
	}

	fetch(`/CodeEditorDemo/files?action=get&fileName=${fileName}&userName=${username}`)
		.then(response => response.json())
		.then(data => {
			if (data.access === "ok") {
				editor.setValue(data.content);
				lastSyncedContent = data.content;
				currentFile = fileName;
				document.getElementById('sync-info').innerText = `Last edited by: ${data.lastEditedBy}`;

				document.querySelectorAll("#file-list li").forEach(li => {
					li.classList.remove("selected-file");
				});

				let selectedFile = document.querySelector(`#file-list li[data-filename="${fileName}"]`);
				if (selectedFile) {
					selectedFile.classList.add("selected-file");
				}
			}
			else {
				alert("You do not have the Access to this file");
			}
		})
		.catch(error => console.error("Error loading file:", error));

}

function checkForUpdates() {
	if (!currentFile) return;

	fetch(`/CodeEditorDemo/files?action=get&fileName=${currentFile}&userName=${username}`)
		.then(response => response.json())
		.then(data => {
			if (data.access === "ok") {
				if (data.content !== lastSyncedContent) {
					lastSyncedContent = data.content;
					editor.setValue(data.content);
					document.getElementById('sync-info').innerText = `Last edited by: ${data.lastEditedBy}`;
					console.log("🔄 File updated automatically!");
				}
			}
		})
		.catch(error => console.error("Error checking updates:", error));
}

function saveFile(fileName, code) {
	fetch('/CodeEditorDemo/files?action=update', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ user: username, name: fileName, code: code })
	})
		.then(response => response.json())
		.then(data => {
			document.getElementById('sync-info').innerText = `Last sync by: ${data.user}`;
			alert("✅ Code Synced Successfully!");
		})
		.catch(error => console.error("Error saving file:", error));
}

