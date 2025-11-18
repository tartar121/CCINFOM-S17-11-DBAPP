package View;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

    // Define your style
    private Font categoryFont = new Font("Arial", Font.BOLD, 15);
    private Font itemFont = new Font("Arial", Font.PLAIN, 14);
    
    private Color sidebarColor; 
    private Color selectionColor = new Color(184, 207, 229); 
    private Color fontColor = new Color(44, 62, 80); 
    
    // Load and RESIZE your icons
    // Helper method to force all icons to 16x16
    private Icon recordsIcon = loadIcon("icons/records.png"); 
    private Icon transactionsIcon = loadIcon("icons/cart.png"); 
    private Icon mgmtIcon = loadIcon("icons/admin.png");      
    private Icon reportsIcon = loadIcon("icons/report.png"); 
    private Icon leafIcon = loadIcon("icons/item.png");      

    
    public CustomTreeCellRenderer(Color sidebarBgColor) {
        this.sidebarColor = sidebarBgColor;
        
        setBackgroundSelectionColor(selectionColor);
        setTextSelectionColor(fontColor);
        setBackgroundNonSelectionColor(sidebarColor);
        setTextNonSelectionColor(fontColor);
        setBorderSelectionColor(null); 
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        String nodeText = (String) node.getUserObject();

        // Set fonts and icon
        if (leaf) {
            setFont(itemFont);
            setIcon(leafIcon);
        } else {
            setFont(categoryFont);
            setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); 
            
            // Set the icon based on the category name
            if (nodeText.contains("Records")) {
                setIcon(recordsIcon);
            } else if (nodeText.contains("Transactions")) {
                setIcon(transactionsIcon);
            } else if (nodeText.contains("Management")) {
                setIcon(mgmtIcon);
            } else if (nodeText.contains("Reports")) {
                setIcon(reportsIcon);
            }
        }
        
        return this;
    }
    
    /**
     * Loads an icon from a path and scales it to a standard 16x16 size.
     * @param path The path to the icon file (e.g., "icons/cart.png")
     * @return A scaled ImageIcon
     */
    private ImageIcon loadIcon(String path) {
        ImageIcon originalIcon = new ImageIcon(path);
        Image originalImage = originalIcon.getImage();
        
        // Scale it to 16x16 pixels
        Image scaledImage = originalImage.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        
        return new ImageIcon(scaledImage);
    }
}