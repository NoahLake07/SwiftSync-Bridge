package com.swiftsync.bridge.backend.scan;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;

public class FileScanResult {

    ArrayList<File> files = new ArrayList<>();

    public FileScanResult(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile.toString());
            doc.getDocumentElement().normalize();

            NodeList fileNodes = doc.getElementsByTagName("file");
            for (int i = 0; i < fileNodes.getLength(); i++) {
                Element fileElement = (Element) fileNodes.item(i);
                String name = fileElement.getAttribute("name");
                String path = fileElement.getAttribute("path");
                long size = Long.parseLong(fileElement.getAttribute("size"));
                files.add(new File(name, path, size));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFileAttributes(String pathFromRoot) {
        for (File file : files) {
            if (file.path.equals(pathFromRoot)) {
                return file;
            }
        }
        return null;
    }

    class File {
        String name;
        String path;
        long size;

        public File(String name, String path, long size) {
            this.name = name;
            this.path = path;
            this.size = size;
        }
    }
}