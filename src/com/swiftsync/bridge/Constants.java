package com.swiftsync.bridge;

import java.io.File;
import java.nio.file.Paths;

public class Constants {

    public static final String SERVER_ADRRESS = "0.0.0.0";
    public static final int SERVER_PORT = 8080;
    public static final String VERSION = "BETA 1.0.0";

    public static final String userHome = System.getProperty("user.home");
    public static final String documentsFolder = Paths.get(userHome, "Documents").toString();
    public static final String swiftSyncFolder = Paths.get(documentsFolder, ".swiftsync-bridge").toString();
    public static final String projectsFolder = Paths.get(swiftSyncFolder, "projects").toString();
        public static final File userInfoFile = new File(Paths.get(swiftSyncFolder, "user-info.json").toString());

    public static final String projectExtension = ".ssb";

    static {
        // Create SwiftSync Bridge directory if it doesn't exist
        File swiftSyncFolderFile = new File(swiftSyncFolder);
        if(!swiftSyncFolderFile.exists()) {
            swiftSyncFolderFile.mkdirs();
        }

        // Create projects directory if it doesn't exist
        File projectsFolderFile = new File(projectsFolder);
        if(!projectsFolderFile.exists()) {
            projectsFolderFile.mkdirs();
        }
    }

}
