package pl.dawad.settings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

public class SettingsManager {
    private final String SETTINGS_FILE_PATH = "settings/settings.xml";
    public void saveSettings(int pdfViewQuality, List<String> lastUsagePaths, int lastSelectedPathIndex) {
        AppSettings settings = new AppSettings();
        settings.setPdfViewQuality(pdfViewQuality);
        settings.setLastUsagePaths(lastUsagePaths);
        settings.setLastSelectedPathIndex(lastSelectedPathIndex);

        try {
            JAXBContext context = JAXBContext.newInstance(AppSettings.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(settings, new File(SETTINGS_FILE_PATH));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public AppSettings loadSettings() {
        File file = new File(SETTINGS_FILE_PATH);

        try {
            JAXBContext context = JAXBContext.newInstance(AppSettings.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return (AppSettings) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
