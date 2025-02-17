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

	setInterval(checkForUpdates, 5000);

});

function loadFiles() {
	fetch('/CodeEditorDemo/files?action=list')
		.then(response => response.json())
		.then(files => {
			let fileList = document.getElementById('file-list');
			fileList.innerHTML = "";
			files.forEach(file => {
				let li = document.createElement("li");
				li.textContent = file;
				li.setAttribute("data-filename", file);
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
	fetch('/CodeEditorDemo/files', {
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

