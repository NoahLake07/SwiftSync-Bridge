package com.swiftsync.bridge.client;

import com.swiftsync.bridge.Constants;
import com.swiftsync.bridge.backend.Util;

import javax.swing.*;
import java.io.File;

public class ProjectFrame extends JFrame {

    Project project;
    SessionClient client;
    String username;

    public ProjectFrame(String code, String localRoot){
        project = new Project(code, localRoot);
        setupUI();
    }

    public ProjectFrame(File projectFile){
        project = new Project(projectFile);
        setupUI();
    }

    private void setupConnection(){
        client = new SessionClient(Constants.SERVER_ADRRESS,Constants.SERVER_PORT);
        client.startConnection();

        client.sendMessage("JOIN_PROJECT*" + project.projectCode + "*" + (username = Util.getUsername()));
        String recievedMessage = "";
        while(!recievedMessage.equals("JOIN_PROJECT_SUCCESS")){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            recievedMessage = client.receiveMessage();
        }

        startListening();
    }

    private void setupUI(){
        setSize(800, 600);
        setTitle(project.projectTitle + " - SwiftSync Bridge");

        // TODO setup panels & components

        setupConnection();
        setVisible(true);
    }

    private void newMessage(String message){
        // TODO handle messages
    }

    private void startListening(){
        new Thread(() -> {
            while(true){
                String message = client.receiveMessage();
                if(message != null){
                    newMessage(message);
                }
            }
        }).start();
    }
}
