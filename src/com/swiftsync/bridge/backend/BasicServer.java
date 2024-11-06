package com.swiftsync.bridge.backend;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class BasicServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    public BasicServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            System.out.println("Waiting for a client to connect...");
            clientSocket = serverSocket.accept();
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            System.out.println("Client connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            return new String(buffer, 0, bytesRead);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

