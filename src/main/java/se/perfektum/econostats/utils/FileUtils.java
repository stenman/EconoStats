package se.perfektum.econostats.utils;

import java.io.IOException;

public class FileUtils {
    public static java.io.File saveFileLocally(String filePath, String content) throws IOException {
        org.apache.commons.io.FileUtils.writeStringToFile(new java.io.File(filePath), content, "UTF-8");
        return new java.io.File(filePath);
    }
}
