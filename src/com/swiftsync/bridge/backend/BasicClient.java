package com.swiftsync.bridge.backend;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class BasicClient {
    private Socket socket;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private String serverAddress;
    private int serverPort;

    public BasicClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void startConnection() {
        try {
            URI uri = new URI(serverAddress);
            String host = uri.getHost() != null ? uri.getHost() : uri.getPath();
            int port = uri.getPort() != -1 ? uri.getPort() : serverPort;
            this.socket = new Socket(host, port);
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            this.in = new BufferedInputStream(this.socket.getInputStream());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            // Converts the message to bytes and sends as a binary stream
            out.write(message.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file, int bufferSize) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;

            // Send the file data in chunks
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            System.out.println("File sent successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1024]; // Buffer for reading messages
            int bytesRead = in.read(buffer);
            return new String(buffer, 0, bytesRead);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

