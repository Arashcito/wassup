package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Set<ClientHandler> clients;
    private String username;
    private Consumer<String> logger;

    public ClientHandler(Socket socket, Set<ClientHandler> clients, Consumer<String> logger) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.logger = logger;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            username = in.readLine();
            broadcast(username + " joined the chat!");
            log(username + " connected.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("/exit".equalsIgnoreCase(inputLine)) break;
                broadcast(username + ": " + inputLine);
            }
        } catch (IOException e) {
            log("Error with " + username + ": " + e.getMessage());
        } finally {
            try {
                clients.remove(this);
                broadcast(username + " left the chat.");
                log(username + " disconnected.");
                clientSocket.close();
            } catch (IOException e) {
                log("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void broadcast(String message) {
        clients.forEach(client -> client.out.println(message));
    }

    private void log(String message) {
        if (logger != null) logger.accept(message);
    }
}