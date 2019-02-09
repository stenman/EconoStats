package se.perfektum.econostats.utils;

public class SystemUtils {
    public static boolean isWindows() {

        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("win") >= 0);

    }
}
