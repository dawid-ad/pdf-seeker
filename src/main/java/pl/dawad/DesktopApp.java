package pl.dawad;

import pl.dawad.settings.AppSettings;
import pl.dawad.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class DesktopApp extends JFrame {
    private JPanel mainPanel;
    private JTextField wordToFind;
    private JButton searchButton;
    private JToolBar toolbar;
    private JList<String> foundPdfsJList;
    private JLabel pdfLabel;
    private JButton OpenFileButton;
    private JButton OpenFolderPathButton;
    private JProgressBar progressBar;
    private JScrollPane pdfScrollPane;
    private JScrollPane foundPdfsScrollPane;
    private JSlider pdfZoomSlider;
    private float pdfQualityZoom;
    private final JButton setMainFolderPathButton = new JButton();
    private final JComboBox<String> chosenFolderPathBox = new JComboBox<>();
    private final SearchEngineManager searchEngineManager = new SearchEngineManager();
    private final PdfViewer pdfViewer = new PdfViewer(pdfLabel);
    private final SettingsManager settingsManager = new SettingsManager();

    public static void main(String[] args){
        new DesktopApp();
    }

    public DesktopApp(){
        loadAppSettings();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            createUIComponents();
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(mainPanel);
        setToolbar();
        setPdfLabel();
        setTitle("Znajdź PDF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820,500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(true);

        searchButton.addActionListener(e -> handleSearchButton());
        wordToFind.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSearchButton();
                }
            }
        });

        foundPdfsJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handlePdfsListClicked();
            }
        });
        OpenFolderPathButton.addActionListener(e -> openPdf(pdfViewer.getPdfParentFolder()));
        OpenFileButton.addActionListener(e -> openPdf(pdfViewer.getPdfFile()));
        chosenFolderPathBox.addActionListener(e -> searchEngineManager.setMainFolderPath((String) chosenFolderPathBox.getSelectedItem()));
        chosenFolderPathBox.addActionListener(e -> handleCheckedPathBox());
        pdfZoomSlider.addChangeListener(e -> handlePdfZoomChange());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleCLoseEvent();
            }
        });
    }

    private void loadAppSettings(){
        AppSettings loadedSettings = settingsManager.loadSettings();
        pdfZoomSlider.setValue(loadedSettings.getPdfViewQuality());
        handlePdfZoomChange();
        setLastUsagePathsOnPathBox(loadedSettings.getLastUsagePaths(),loadedSettings.getLastSelectedPathIndex());
    }

    private void handlePdfsListClicked() {
        int selectedIndex = foundPdfsJList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = searchEngineManager.getMainFolderPath() +
                    foundPdfsJList.getSelectedValue();
            if (Files.isReadable(Path.of(selectedValue))) {
                pdfViewer.setPdfFilePath(selectedValue);
                setPdfLabel();
            }
        }
    }

    private void handlePdfZoomChange(){
        pdfQualityZoom = (float) 0.5 * pdfZoomSlider.getValue();
        pdfViewer.setImageQuality(pdfQualityZoom);
        handlePdfsListClicked();
    }

    private void handleSearchButton() {
    SwingWorker<List<File>, Void> searchWorker =
        new SwingWorker<>() {
          @Override
          protected List<File> doInBackground() {
            if (searchEngineManager.getMainFolderPath() != null
                && wordToFind.getText().length() > 2) {
              searchEngineManager.setSearchWord(wordToFind.getText());
              searchEngineManager.searchFiles();
              return searchEngineManager.getFoundPdfs();
            }
            return null;
          }

          @Override
          protected void done() {
            try {
              List<File> foundPdfs = get();
              if (foundPdfs != null) {
                setFoundPdfsJList(foundPdfs);
              }
            } catch (Exception ex) {
              ex.printStackTrace();
            } finally {
              progressBar.setIndeterminate(false);
            }
          }
        };
        progressBar.setIndeterminate(true);
        searchWorker.execute();
    }

    private void setFoundPdfsJList(List<File> foundPdfs) {
        if (foundPdfs != null) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (File pdf : foundPdfs) {
                listModel.addElement(pdf.getAbsolutePath().replace(searchEngineManager.getMainFolderPath(),""));
            }
            if(foundPdfs.size() == 0){
                listModel.addElement("Nie znaleziono " + searchEngineManager.getSearchWord());
            }
            foundPdfsJList.setModel(listModel);
        }
    }
    private void openPdf(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            JOptionPane.showMessageDialog(this, "Nie wybrano pliku.");
            return;
        }
        try {
            Desktop.getDesktop().open(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setPdfLabel() {
    SwingWorker<List<File>, Void> pdfViewWorker =
        new SwingWorker<>() {
          @Override
          protected List<File> doInBackground() {
            DraggableImageIcon pdfImage = pdfViewer.getPDFImageIcon();

            if (pdfImage != null) {
              pdfLabel.setIcon(pdfImage);
            }
            return null;
          }

          @Override
          protected void done() {
            progressBar.setIndeterminate(false);
          }
        };
        progressBar.setIndeterminate(true);
        pdfViewWorker.execute();
    }

    private void setToolbar(){
        setMainFolderPathButton.setText("Wybierz folder");
        setMainFolderPathButton.addActionListener(e -> handleChooseFolderButton());
        toolbar.add(setMainFolderPathButton);
        toolbar.add(chosenFolderPathBox);
    }
    private void handleCheckedPathBox(){
        searchEngineManager.setMainFolderPath((String) chosenFolderPathBox.getSelectedItem());
    }

    private void handleChooseFolderButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showDialog(this, "Wybierz");

        if (result == JFileChooser.APPROVE_OPTION) {
            String chosenFolderPath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!isFolderInList(chosenFolderPath)) {
                chosenFolderPathBox.insertItemAt(chosenFolderPath, 0);
                trimFolderPathList();
            }
            chosenFolderPathBox.setSelectedItem(chosenFolderPath);
            searchEngineManager.setMainFolderPath(chosenFolderPath);
        }
    }
    private void setLastUsagePathsOnPathBox(List<String> lastUsagePaths, int lastSelectedPathIndex){
        if(lastUsagePaths != null){
            for (String path : lastUsagePaths) {
                chosenFolderPathBox.insertItemAt(path,0);
            }
            if(lastUsagePaths.size() > 0){
                chosenFolderPathBox.setSelectedIndex(lastSelectedPathIndex);
                searchEngineManager.setMainFolderPath(chosenFolderPathBox.getItemAt(lastSelectedPathIndex));
            }
        }

    }

    private boolean isFolderInList(String chosenFolderPath) {
        for (int i = 0; i < chosenFolderPathBox.getItemCount(); i++) {
            if (chosenFolderPath.equals(chosenFolderPathBox.getItemAt(i))) {
                return true;
            }
        }
        return false;
    }
    private void trimFolderPathList() {
        int maxFolders = 5;
        int currentSize = chosenFolderPathBox.getItemCount();
        if (currentSize > maxFolders) {
            chosenFolderPathBox.removeItemAt(currentSize - 1);
        }
    }
    private void handleCLoseEvent() {
        List<String> lastUsagePaths = new ArrayList<>();
        for(int i = chosenFolderPathBox.getItemCount(); i >= 0; i--) {
            lastUsagePaths.add(chosenFolderPathBox.getItemAt(i));
        }
        int lastSelectedPathIndex = chosenFolderPathBox.getSelectedIndex();
        settingsManager.saveSettings(pdfZoomSlider.getValue(),lastUsagePaths,lastSelectedPathIndex);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
