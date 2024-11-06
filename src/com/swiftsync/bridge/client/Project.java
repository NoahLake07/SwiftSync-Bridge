package com.swiftsync.bridge.client;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Project {

    File projectFile, localRoot;
    File projectInfo, serverChangelog, localScan;
        // Project Info
        int projectCode;
        String projectTitle;
        LocalDateTime lastLocalScan, lastServerFetch;
        File projectRoot;

    public Project(String code, String root){ // for joining a project for the first time
        // fetch info from server

        // create project files
        localRoot = new File(root);

        // use this(projectFile) to open the project

    }

    public Project(File projectFile){ // for opening an existing project
        this.projectFile = projectFile;

        this.projectInfo = Paths.get(projectFile.toString(), "project-info.json").toFile();
        this.serverChangelog = Paths.get(projectFile.toString(), "server-changelog.xml").toFile();
        this.localScan = Paths.get(projectFile.toString(), "local-scan.xml").toFile();

        // parse project info json
        JSONObject projectInfoJson = new JSONObject(projectInfo);
        this.projectCode = projectInfoJson.getInt("project-code");
        this.projectTitle = projectInfoJson.getString("project-title");
        this.lastLocalScan = LocalDateTime.parse(projectInfoJson.getString("last-local-scan"));
        this.lastServerFetch = LocalDateTime.parse(projectInfoJson.getString("last-server-fetch"));
        this.localRoot = Paths.get(projectInfoJson.getString("local-root-path")).toFile();
    }

}
