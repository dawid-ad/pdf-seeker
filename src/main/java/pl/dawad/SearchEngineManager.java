package pl.dawad;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchEngineManager {
    private String mainFolderPath;
    private String searchWord;
    private List<File> foundPdfs;

    public String getMainFolderPath() {
        return mainFolderPath;
    }

    public void setMainFolderPath(String mainFolderPath) {
        this.mainFolderPath = mainFolderPath;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        if(searchWord.length() < 3){
            this.searchWord = null;
        } else {
            this.searchWord = searchWord;
        }
    }

    public List<File> getFoundPdfs() {
        return foundPdfs;
    }
    public boolean containsPdfs(){
        return foundPdfs.size() > 0;
    }

    public void searchFiles(){
        foundPdfs = new ArrayList<>();
        if(searchWord.length() < 3){
            return;
        }
        List<File> pdfFiles = new ArrayList<>();
        getPDFFiles(mainFolderPath, pdfFiles);

        for (File pdfFile : pdfFiles) {
            try {
                String text = extractTextFromPDF(pdfFile);
                if(hasValue(text,searchWord)){
                    foundPdfs.add(pdfFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getPDFFiles(String path, List<File> pdfFiles) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;
        for (File file : list) {
            if (file.isDirectory()) {
                getPDFFiles(file.getAbsolutePath(), pdfFiles);
            } else {
                if (file.getName().endsWith(".pdf")) {
                    pdfFiles.add(file);
                }
            }
        }
    }
    private String extractTextFromPDF(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            return pdfTextStripper.getText(document);
        }
    }
    private boolean hasValue (String textFromPdf, String searchWord){
        if (textFromPdf == null || searchWord == null) {
            return false;
        }
        final int length = searchWord.length();
        if (length <= 1) {
            return false;
        }
        for (int i = textFromPdf.length() - length; i >= 0; i--) {
            if (textFromPdf.regionMatches(true, i, searchWord, 0, length)) {
                return true;
            }
        }
        return false;
    }
}
