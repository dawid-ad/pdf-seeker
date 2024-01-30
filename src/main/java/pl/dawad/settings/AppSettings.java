package pl.dawad.settings;

import java.util.ArrayList;
import java.util.List;

public class AppSettings {
    private int pdfViewQuality;
    private List<String> lastUsagePaths;
    private int lastSelectedPathIndex;

    public AppSettings() {
        lastUsagePaths = new ArrayList<>();
    }

    public int getPdfViewQuality() {
        return pdfViewQuality;
    }

    public void setPdfViewQuality(int pdfViewQuality) {
        this.pdfViewQuality = pdfViewQuality;
    }

    public List<String> getLastUsagePaths() {
        return lastUsagePaths;
    }

    public int getLastSelectedPathIndex() {
        return lastSelectedPathIndex;
    }

    public void setLastSelectedPathIndex(int lastSelectedPathIndex) {
        this.lastSelectedPathIndex = lastSelectedPathIndex;
    }

    public void setLastUsagePaths(List<String> lastUsagePaths) {
        this.lastUsagePaths = lastUsagePaths;
    }
}
