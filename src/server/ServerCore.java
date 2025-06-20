package server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerCore {
    private Set<ClientHandler> clients = new HashSet<>();
    private JTextArea logArea;

    public void start(int port) {
        JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);
        frame.setVisible(true);

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                log("Server started on port " + port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    log("New client connected: " + clientSocket);

                    ClientHandler clientThread = new ClientHandler(clientSocket, clients, this::log);
                    clients.add(clientThread);
                    clientThread.start();
                }
            } catch (IOException e) {
                log("Server error: " + e.getMessage());
            }
        }).start();
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }
}