package com.swiftsync.bridge.backend;

import com.swiftsync.bridge.Constants;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Util {

    public static String trimPathToRoot(String path, String root) {
        if(path.startsWith(root)) {
            return path.substring(root.length());
        }
        return path;
    }

    public static String getUsername() {
        File userFile = new File(Constants.userInfoFile.toString());
        if(userFile.exists()) {
            // Read the username from the user-info.json file
            JSONObject userInfo = new JSONObject(userFile);
            return userInfo.getString("username");
        }
        return null;
    }

}
