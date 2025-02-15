package com.codeeditor.server;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/codeEditor")
public class CodeEditorEndpoint {
	
	private static Set<Session> clients = new CopyOnWriteArraySet<>();
	
	@OnOpen
	public void onOpen(Session session) {
		clients.add(session);
	}
	
	 @OnMessage
	    public void onMessage(String message, Session sender) {
	        // Broadcast changes to all connected users
	        for (Session client : clients) {
	            if (!client.equals(sender)) {
	                try {
	                    client.getBasicRemote().sendText(message);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	
	@OnClose
	public void onClose(Session session) {
		clients.remove(session);
	}
	
}
