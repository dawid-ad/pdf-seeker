package pl.dawad;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfViewer {
    private String pdfFilePath;
    private float imageQuality;
    private Point origin = new Point();
    private JLabel pdfLabel;
    private DraggableImageIcon pdfImage;

    public PdfViewer(JLabel pdfLabel) {
        this.pdfLabel = pdfLabel;
    }
    public String getPdfFilePath() {
        return pdfFilePath;
    }
    public File getPdfFile(){
        if(pdfFilePath == null){
            return null;
        }
        return new File(pdfFilePath);
    }
    public File getPdfParentFolder(){
        if(pdfFilePath == null){
            return null;
        }
        return new File(getPdfFile().getParent());
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public float getImageQuality() {
        return imageQuality;
    }

    public void setImageQuality(float imageQuality) {
        this.imageQuality = imageQuality;
    }

    public BufferedImage convertPDFToImage() {
        try {
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int pageCount = document.getNumberOfPages();
            int totalWidth = 0;
            int totalHeight = 0;

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                BufferedImage pageImage = pdfRenderer.renderImage(pageIndex, imageQuality);
                totalWidth = Math.max(totalWidth, pageImage.getWidth());
                totalHeight += pageImage.getHeight();

                if (pageIndex < pageCount - 1) {
                    totalHeight += 10;
                }
            }

            BufferedImage combinedImage = new BufferedImage(
                    totalWidth,
                    totalHeight,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = combinedImage.createGraphics();
            int currentY = 0;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                BufferedImage pageImage = pdfRenderer.renderImage(pageIndex, imageQuality);
                g.drawImage(pageImage, 0, currentY, null);
                currentY += pageImage.getHeight();

                if (pageIndex < pageCount - 1) {
                    currentY += 10;
                }
            }

            g.dispose();
            document.close();

            return combinedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private MouseAdapter pdfLabelMouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            origin = new Point(e.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            origin = null;
        }
    };

    private MouseAdapter pdfLabelMouseMotionListener = new MouseAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (origin != null) {
                Point current = new Point(e.getPoint());
                int offsetX = current.x - origin.x;
                int offsetY = current.y - origin.y;

                origin = current;

                int totalOffsetX = pdfImage.getOffsetX() + offsetX;
                int totalOffsetY = pdfImage.getOffsetY() + offsetY;

                pdfImage.setOffset(totalOffsetX, totalOffsetY);
                pdfLabel.repaint();
            }
        }
    };

    public DraggableImageIcon getPDFImageIcon() {
        if (pdfFilePath == null) {
            return null;
        }
        BufferedImage combinedImage = convertPDFToImage();

        if (combinedImage != null) {
            origin = null;
            pdfLabel.addMouseListener(pdfLabelMouseListener);
            pdfLabel.addMouseMotionListener(pdfLabelMouseMotionListener);
            pdfImage = new DraggableImageIcon(combinedImage);
            return pdfImage;
        } else {
            pdfLabel.removeMouseListener(pdfLabelMouseListener);
            pdfLabel.removeMouseMotionListener(pdfLabelMouseMotionListener);
            return null;
        }
    }

}
