package View;

// All 12 Panel Imports (Just put View.* for simplicity)
import View.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class NewMainView extends JFrame {
    
    private JSplitPane splitPane;
    private JTree navigationTree;
    private JPanel mainPanel; 
    private CardLayout cardLayout;

    // All 12 Content panels
    private MedicinePanel medicinePanel; 
    private SupplierPanel supplierPanel;
    private CustomerPanel customerPanel;
    private CreatePurchasePanel createPurchasePanel;
    private CreateDeliveryPanel createDeliveryPanel; 
    private CreateReturnPanel createReturnPanel;
    private CustomerPurchaseReportPanel customerPurchaseReportPanel;
    private MedicineReturnReportPanel medicineReturnReportPanel;
    private ProcurementReportPanel procurementReportPanel;
    private PurchasePanel purchasePanel;
    private DeliveryPanel deliveryPanel;
    private ReturnPanel returnPanel;

    public NewMainView() {
        super("Pharmacy Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); 
        setLocationRelativeTo(null);
        
        // Set Up Colors
        Color sidebarColor = new Color(240, 240, 240); // Light gray
        Color contentColor = Color.WHITE;
        Color borderColor = new Color(220, 220, 220);
        
        // Add Taskbar Icon
        ImageIcon icon = new ImageIcon("icons/logo.png");
        setIconImage(icon.getImage());
        
        // Create JTree (Left Side) for Navigation
        navigationTree = new JTree(createTreeNodes());
        navigationTree.setBackground(sidebarColor);
        navigationTree.setRootVisible(false); 
        for (int i = 0; i < navigationTree.getRowCount(); i++) {
            navigationTree.expandRow(i);
        }
        navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Apply Custom Renderer
        navigationTree.setCellRenderer(new CustomTreeCellRenderer(sidebarColor));
        navigationTree.setRowHeight(25); // Give the items more space

        JScrollPane treeScrollPane = new JScrollPane(navigationTree);
        treeScrollPane.getViewport().setBackground(sidebarColor);
        treeScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor));
        treeScrollPane.setMinimumSize(new Dimension(240, 100));

        // All 12 Panels Initialization and their Background Color
        medicinePanel = new MedicinePanel(this);
        medicinePanel.setBackground(contentColor);
        supplierPanel = new SupplierPanel(this);
        supplierPanel.setBackground(contentColor);
        customerPanel = new CustomerPanel(this);
        customerPanel.setBackground(contentColor);
        
        createPurchasePanel = new CreatePurchasePanel(this);
        createPurchasePanel.setBackground(contentColor);
        createDeliveryPanel = new CreateDeliveryPanel(this);
        createDeliveryPanel.setBackground(contentColor);
        createReturnPanel = new CreateReturnPanel(this);
        createReturnPanel.setBackground(contentColor);
        
        purchasePanel = new PurchasePanel(this);
        purchasePanel.setBackground(contentColor);
        deliveryPanel = new DeliveryPanel(this);
        deliveryPanel.setBackground(contentColor);
        returnPanel = new ReturnPanel(this);
        returnPanel.setBackground(contentColor);
        
        customerPurchaseReportPanel = new CustomerPurchaseReportPanel(this);
        customerPurchaseReportPanel.setBackground(contentColor);
        procurementReportPanel = new ProcurementReportPanel(this);
        procurementReportPanel.setBackground(contentColor);
        medicineReturnReportPanel = new MedicineReturnReportPanel(this);
        medicineReturnReportPanel.setBackground(contentColor);

        // Create Card Layout Panel (Right Side)
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(contentColor);
        mainPanel.setBorder(null);

        // Add a blank "Welcome" panel with watermark
        // 15% opacity (0.15f)
        ImagePanel welcomePanel = new ImagePanel(icon, 0.15f);
        welcomePanel.setBackground(contentColor); // Match the color
        
        JLabel welcomeLabel = new JLabel("Welcome to the Pharmacy Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(100, 100, 100));
        
        // Make the label's background transparent so the watermark image can be seen behind it
        welcomeLabel.setOpaque(false); 
        
        welcomePanel.add(welcomeLabel); // The GridBagLayout will center it
        
        mainPanel.add(welcomePanel, "welcome"); // This is the default panel
        
        // Add all 12 panels, using their *name* as the "key"
        mainPanel.add(medicinePanel, "Medicines");
        mainPanel.add(supplierPanel, "Suppliers");
        mainPanel.add(customerPanel, "Customers");
        mainPanel.add(createPurchasePanel, "Make A Purchase");
        mainPanel.add(createDeliveryPanel, "Request Delivery");
        mainPanel.add(createReturnPanel, "Request Return");     
        mainPanel.add(purchasePanel, "Manage Purchases");
        mainPanel.add(deliveryPanel, "Manage Deliveries");
        mainPanel.add(returnPanel, "Manage Returns");
        mainPanel.add(customerPurchaseReportPanel, "Customer Purchase");
        mainPanel.add(procurementReportPanel, "Procurement (Delivery)");
        mainPanel.add(medicineReturnReportPanel, "Medicine Return");

        cardLayout.show(mainPanel, "welcome");

        // Create Split Plant for Navigation and Main Panel
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, mainPanel);
        splitPane.setDividerLocation(250); // Set the initial width of the left panel
        splitPane.setBorder(null); // Remove the 3D border

        // Add Tree Click Listener
        navigationTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = 
                (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
            
            if (selectedNode == null || !selectedNode.isLeaf()) {
                return; // Do nothing if they click a category
            }
            String panelName = selectedNode.getUserObject().toString();
            cardLayout.show(mainPanel, panelName);
        });

        // Add Split Pane to Frame
        add(splitPane);
    }

    // Helper method to build the tree (Updated names)
    private DefaultMutableTreeNode createTreeNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        DefaultMutableTreeNode records = new DefaultMutableTreeNode("Records Management");
        records.add(new DefaultMutableTreeNode("Medicines"));
        records.add(new DefaultMutableTreeNode("Suppliers"));
        records.add(new DefaultMutableTreeNode("Customers"));
        root.add(records);

        DefaultMutableTreeNode transactions = new DefaultMutableTreeNode("Transactions");
        transactions.add(new DefaultMutableTreeNode("Make A Purchase"));
        transactions.add(new DefaultMutableTreeNode("Request Delivery"));
        transactions.add(new DefaultMutableTreeNode("Request Return"));
        root.add(transactions);

        DefaultMutableTreeNode mgmt = new DefaultMutableTreeNode("Transaction Management");
        mgmt.add(new DefaultMutableTreeNode("Manage Purchases"));
        mgmt.add(new DefaultMutableTreeNode("Manage Deliveries"));
        mgmt.add(new DefaultMutableTreeNode("Manage Returns"));
        root.add(mgmt);

        DefaultMutableTreeNode reports = new DefaultMutableTreeNode("Reports");
        reports.add(new DefaultMutableTreeNode("Customer Purchase"));
        reports.add(new DefaultMutableTreeNode("Procurement (Delivery)"));
        reports.add(new DefaultMutableTreeNode("Medicine Return"));
        root.add(reports);

        return root;
    }

    public void goHome() {
        // This method is no longer needed.
    }
}