package View;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel that can draw a background image with a
 * specified opacity, centered in the panel.
 */
public class ImagePanel extends JPanel {
    private Image image;
    private float opacity;

    /**
     * Creates a new ImagePanel.
     * @param icon The image to display.
     * @param opacity The opacity of the image (0.0f to 1.0f).
     */
    public ImagePanel(ImageIcon icon, float opacity) {
        this.image = icon.getImage();
        this.opacity = opacity;
        // Use GridBagLayout to easily center the components (like your JLabel)
        setLayout(new GridBagLayout()); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // This paints the regular background (e.g., Color.WHITE)
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        // 1. Set the low opacity
        // This is the "fading" effect you wanted
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        // 2. Calculate coordinates to center the image
        int x = (this.getWidth() - image.getWidth(null)) / 2;
        int y = (this.getHeight() - image.getHeight(null)) / 2;
        
        // 3. Draw the watermark
        g2d.drawImage(image, x, y, null);
        
        // 4. Set opacity back to normal so children (the label) paint correctly
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        g2d.dispose();
    }
}