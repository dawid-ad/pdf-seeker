package pl.dawad;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

class DraggableImageIcon extends ImageIcon {
    private int offsetX = 0;
    private int offsetY = 0;
    private float zoom;

    public DraggableImageIcon(Image image) {
        super(image);
    }

    public void setOffset(int x, int y) {
        offsetX = x;
        offsetY = y;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(getImage(), AffineTransform.getTranslateInstance(offsetX, offsetY), (ImageObserver) c);
        g2d.dispose();
    }
}
