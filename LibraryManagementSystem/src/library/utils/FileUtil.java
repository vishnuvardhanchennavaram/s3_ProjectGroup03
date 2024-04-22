package library.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static FileUtil instance;

    // Private constructor to prevent external instantiation
    private FileUtil() {}

    // Public method to get the instance
    public static FileUtil getInstance() {
        if (instance == null) {
            synchronized (FileUtil.class) {
                if (instance == null) {
                    instance = new FileUtil();
                }
            }
        }
        return instance;
    }

    public List<String> readFile(String filePath) {
        List<String> csvLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                csvLines.add(line);
            }
        } catch (IOException e) {
            System.out.println("IOException: Error while reading file " + e);
        }
        return csvLines;
    }
	
    public static boolean writeToCSV(String filePath, List<String> data) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            for (String entry : data) {
                writer.append(entry).append("\n");
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write to CSV: " + e.getMessage());
            return false;
        }
    }
	
    public static boolean writeToCSV1(String filePath, List<String> data) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            for (String entry : data) {
                writer.append(entry).append("\n");
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write to CSV: " + e.getMessage());
            return false;
        }
    }
}
