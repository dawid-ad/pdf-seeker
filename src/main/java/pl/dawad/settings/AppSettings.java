package pl.dawad.settings;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AppSettings {
    private int pdfViewQuality;
    private List<String> lastUsagePaths;
    private int lastSelectedPathIndex;

    public int getPdfViewQuality() {
        return pdfViewQuality;
    }

    @XmlElement
    public void setPdfViewQuality(int pdfViewQuality) {
        this.pdfViewQuality = pdfViewQuality;
    }

    public List<String> getLastUsagePaths() {
        return lastUsagePaths;
    }

    public int getLastSelectedPathIndex() {
        return lastSelectedPathIndex;
    }
    @XmlElement(name = "lastSelectedPathIndex")
    public void setLastSelectedPathIndex(int lastSelectedPathIndex) {
        this.lastSelectedPathIndex = lastSelectedPathIndex;
    }

    @XmlElementWrapper(name = "lastUsagePaths")
    @XmlElement(name = "path")
    public void setLastUsagePaths(List<String> lastUsagePaths) {
        this.lastUsagePaths = lastUsagePaths;
    }
}
