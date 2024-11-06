package com.swiftsync.bridge.client;
import com.swiftsync.bridge.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.swiftsync.bridge.backend.BasicClient;
import org.json.*;

public class BridgeClient extends JFrame {

    BasicClient basicClient;
    private JDialog loadDialog;

    public BridgeClient() {
        // Set FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Initial setup for JFrame
        setTitle("SwiftSync Bridge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        displayLoadingDialog();
        basicClient = new BasicClient(Constants.SERVER_ADRRESS,Constants.SERVER_PORT);
        basicClient.startConnection();

        boolean userInfoExists = Files.exists(Paths.get(Constants.userInfoFile.toURI()));
        System.out.println("User Profile Exists: " + userInfoExists); // * DEBUG
        if(!userInfoExists) {
            showWelcomePage();
        } else {
            showOpenProjectDialog();
        }
    }

    private void displayLoadingDialog(){
        ExecutorService thread = Executors.newCachedThreadPool();
        thread.submit(()->{
            // DISPLAY LOADING DIALOG
            loadDialog = new JDialog((Frame) null, "Loading...", true);
            loadDialog.setLayout(new BorderLayout());
            loadDialog.setSize(300, 150);
            loadDialog.setLocationRelativeTo(null);
            loadDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            loadDialog.setUndecorated(true);
            loadDialog.getContentPane().setBackground(new Color(230, 238, 255));

            loadDialog.setShape(new RoundRectangle2D.Double(0, 0, 300, 150, 30, 30));

            JPanel contents = new JPanel();
            contents.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
            contents.setBackground(new Color(230, 238, 255)); // Match dialog background color
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.insets = new Insets(10, 10, 10, 10); // Add padding
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel loadLabel = new JLabel("SwiftSync Bridge");
            loadLabel.setHorizontalAlignment(JLabel.CENTER);
            loadLabel.setFont(new Font("Arial", Font.BOLD, 16));
            contents.add(loadLabel, gbc);

            JLabel verLabel = new JLabel(Constants.VERSION);
            verLabel.setHorizontalAlignment(JLabel.CENTER);
            verLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            verLabel.setForeground(new Color(86, 86, 86));
            contents.add(verLabel, gbc);

            loadDialog.add(contents, BorderLayout.CENTER);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 15));
            progressBar.setBackground(new Color(31, 134, 189));
            loadDialog.add(progressBar, BorderLayout.SOUTH);

            loadDialog.setVisible(true);
        });
    }

    // Welcome Page
    private void showWelcomePage() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BorderLayout(10, 10));  // Add padding between elements
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel

        JLabel welcomeLabel = new JLabel("Welcome to SwiftSync Bridge", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));  // Modern font style
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout(5, 5));  // Adjust spacing for consistency
        JLabel usernameLabel = new JLabel("Create Account Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField usernameField = new JTextField(15);
        userPanel.add(usernameLabel, BorderLayout.NORTH);
        userPanel.add(usernameField, BorderLayout.CENTER);

        welcomePanel.add(userPanel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nextButton.addActionListener(e -> {
            String username = usernameField.getText();

            // confirm create account with username
            if(JOptionPane.showConfirmDialog(this, "Create account with username: " + username, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    /// Ensure the directory exists
                    Files.createDirectories(Paths.get(Constants.userInfoFile.toURI()).getParent());

                    // Create and write user info JSON file
                    JSONObject userInfo = new JSONObject();
                    userInfo.put("username", username);
                    userInfo.put("system", System.getProperty("os.name"));
                    userInfo.put("device-name", System.getProperty("user.name"));

                    Files.write(Paths.get(Constants.userInfoFile.toURI()), userInfo.toString().getBytes());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                showOpenProjectDialog();
            }
        });
        welcomePanel.add(nextButton, BorderLayout.SOUTH);

        setContentPane(welcomePanel);
        revalidate();
    }

    // Open Project Dialog
    private void showOpenProjectDialog() {
        JPanel openProjectPanel = new JPanel(new BorderLayout(10, 10));
        openProjectPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel openProjectLabel = new JLabel("Open Project", SwingConstants.CENTER);
        openProjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        openProjectPanel.add(openProjectLabel, BorderLayout.NORTH);

        DefaultListModel<String> projectListModel = new DefaultListModel<>();
        HashMap<String,File> projectMap = new HashMap<>();
        File[] projects = new File(Constants.projectsFolder).listFiles();
        if(projects != null) {
            for(File project : projects) {
                if(project.isDirectory()) {
                    File projectInfo = new File(Paths.get(project.getAbsolutePath(), "project-info.json").toString());
                    if(projectInfo.exists()) {
                        try {
                            JSONObject projectJSON = new JSONObject(new String(Files.readAllBytes(Paths.get(projectInfo.toURI()))));
                            projectListModel.addElement(projectJSON.getString("name"));
                            projectMap.put(projectJSON.getString("name"), project);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }

        JList<String> projectList = new JList<>(projectListModel);
        projectList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        openProjectPanel.add(new JScrollPane(projectList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        createButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        createButton.addActionListener(e -> showCreateProjectDialog());
        JButton openButton = new JButton("Open");
        openButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        openButton.addActionListener(e -> launchProject(projectMap.get(projectList.getSelectedValue())));
        buttonPanel.add(createButton);
        buttonPanel.add(openButton);

        openProjectPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(openProjectPanel);
        revalidate();
    }

    private void launchProject(File project) {
        if(project != null) {
            boolean connectedToServer = testConnection();

            if(!connectedToServer) {
                JOptionPane.showMessageDialog(this, "Failed to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                new ProjectFrame(project);
                this.setVisible(false);
            }
        }
    }

    private void launchProject(String projectCode, String localRoot) {
        boolean connectedToServer = testConnection();

        if(!connectedToServer) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            new ProjectFrame(projectCode, localRoot);
            this.setVisible(false);
        }
    }

    // Create New Project Dialog
    private void showCreateProjectDialog() {
        JDialog createProjectDialog = new JDialog(this, "Create New Project", true);
        createProjectDialog.setSize(350, 200);
        createProjectDialog.setLocationRelativeTo(this);

        JPanel createProjectPanel = new JPanel();
        createProjectPanel.setLayout(new GridLayout(3, 2, 10, 10));
        createProjectPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel projectNameLabel = new JLabel("Project Name:");
        projectNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField projectNameField = new JTextField();
        JLabel localRootLabel = new JLabel("Local Root:");
        localRootLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField localRootField = new JTextField();
        JButton chooseButton = new JButton("Choose");

        chooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                localRootField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JButton createButton = new JButton("Create");
        createButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        createButton.addActionListener(e -> {
            createProjectDialog.dispose();
            String code = requestNewProject(projectNameField.getText());
            showProjectCodeDialog(code);
            launchProject(code, localRootField.getText());
        });

        createProjectPanel.add(projectNameLabel);
        createProjectPanel.add(projectNameField);
        createProjectPanel.add(localRootLabel);
        createProjectPanel.add(localRootField);
        createProjectPanel.add(chooseButton);
        createProjectPanel.add(createButton);

        createProjectDialog.add(createProjectPanel);
        createProjectDialog.setVisible(true);
    }

    private boolean testConnection() {
        basicClient.sendMessage("test connection");
        String response = basicClient.receiveMessage();
        int count = 0;
        while(!response.equals("test success")) {
            try {
                Thread.sleep(100);
                if(count++ > 10) {
                    return false; // Timed out
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private String requestNewProject(String projectName) {
        basicClient.sendMessage("new project :" + projectName);
        String code = "";
        while(!(code = basicClient.receiveMessage()).startsWith("new project code")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // remove message tag
        code = code.split(" ")[3];

        System.out.println("New Project Code: " + code); // * DEBUG
        return code;
    }

    // New Project Code Dialog
    private void showProjectCodeDialog(String projectCode) {
        JDialog codeDialog = new JDialog(this, "Project Code", true);
        codeDialog.setSize(250, 150);
        codeDialog.setLocationRelativeTo(this);

        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BorderLayout(10, 10));
        codePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel codeLabel = new JLabel("Your new project code is:");
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel codeValue = new JLabel(projectCode, SwingConstants.CENTER);
        codeValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        okButton.addActionListener(e -> codeDialog.dispose());

        codePanel.add(codeLabel, BorderLayout.NORTH);
        codePanel.add(codeValue, BorderLayout.CENTER);
        codePanel.add(okButton, BorderLayout.SOUTH);

        codeDialog.add(codePanel);
        codeDialog.setVisible(true);
    }

    public static void main(String[] args) {
        BridgeClient app = new BridgeClient();
        app.setVisible(true);
    }
}
