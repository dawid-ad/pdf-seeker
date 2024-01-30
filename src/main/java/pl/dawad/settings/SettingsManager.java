package pl.dawad.settings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingsManager {
    private final String SETTINGS_FOLDER_PATH = "settings";
    private final String SETTINGS_FILE_PATH = SETTINGS_FOLDER_PATH+"/settings.properties";

    public SettingsManager() {
        createSettingsFolder();
    }

    private void createSettingsFolder() {
        File folder = new File(SETTINGS_FOLDER_PATH);
        File file = new File(SETTINGS_FILE_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            saveSettings(5,new ArrayList<>(),-1);
        }
    }

    public void saveSettings(int pdfViewQuality, List<String> lastUsagePaths, int lastSelectedPathIndex) {
        Properties properties = new Properties();
        properties.setProperty("pdfViewQuality", String.valueOf(pdfViewQuality));
        properties.setProperty("lastSelectedPathIndex", String.valueOf(lastSelectedPathIndex));

        if(lastUsagePaths != null){
            for (int i = 0; i < lastUsagePaths.size(); i++) {
                if(lastUsagePaths.get(i) != null){
                    properties.setProperty("lastUsagePath_" + i, lastUsagePaths.get(i));
                } else {
                    break;
                }
            }
        }
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE_PATH)) {
            properties.store(output, "Application Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AppSettings loadSettings() {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(SETTINGS_FILE_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        AppSettings settings = new AppSettings();
        settings.setPdfViewQuality(Integer.parseInt(properties.getProperty("pdfViewQuality")));

        int lastSelectedPathIndex = Integer.parseInt(properties.getProperty("lastSelectedPathIndex"));
        settings.setLastSelectedPathIndex(lastSelectedPathIndex);

        List<String> lastUsagePaths = new ArrayList<>();
        int i = 0;

        while (properties.containsKey("lastUsagePath_" + i)) {
            lastUsagePaths.add(properties.getProperty("lastUsagePath_" + i));
            i++;
        }
        settings.setLastUsagePaths(lastUsagePaths);

        return settings;
    }
}
