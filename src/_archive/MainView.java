package _archive;

// OLD VIEW GUI, DO NOT USE FOR FINAL PRODUCT
// import all panels from View package
import View.*;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel homePanel; // panel for navigation
    
    // Record Management Panels
    private MedicinePanel medicinePanel;
    private SupplierPanel supplierPanel;
    private CustomerPanel customerPanel;

    /* Transaction Record Panels (File Cabinet-ish)
    // Do not add since it is not Transaction User View
    //private PurchasePanel purchasePanel;
    //private DeliveryPanel deliveryPanel;
    //private ReturnPanel returnPanel;
    */

    // Transaction Panels
    private CreatePurchasePanel createPurchasePanel;
    private CreateDeliveryPanel createDeliveryPanel;
    private CreateReturnPanel createReturnPanel;

    // Report Panels 
    private CustomerPurchaseReportPanel cpReportPanel;
    private MedicineReturnReportPanel returnReportPanel;
    private ProcurementReportPanel proReportPanel;

    public MainView() {
        setTitle("Pharmacy Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Home Panel Setup
        homePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Add padding between all buttons
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons fill horizontally and same width

        // Row 0: Core Records Management (Section 3.0)
        gbc.gridy = 0; // Y position 0 (top row)
        
        gbc.gridx = 0; // X position 0
        JButton medButton = new JButton("Medicine Management");
        medButton.addActionListener(e -> cardLayout.show(mainPanel, "medicinePanel"));
        homePanel.add(medButton, gbc);

        gbc.gridx = 1; // X position 1
        JButton supButton = new JButton("Supplier Management");
        supButton.addActionListener(e -> cardLayout.show(mainPanel, "supplierPanel"));
        homePanel.add(supButton, gbc);
        
        gbc.gridx = 2; // X position 2
        JButton cusButton = new JButton("Customer Management");
        cusButton.addActionListener(e -> cardLayout.show(mainPanel, "customerPanel"));
        homePanel.add(cusButton, gbc);

        // Row 1: Transactions (Section 4.0)
        gbc.gridy = 1; // Y position 1 (second row)
        gbc.gridx = 0; // X position 0
        JButton createPurButton = new JButton("Purchase a Medicine");
        createPurButton.addActionListener(e -> cardLayout.show(mainPanel, "createPurchasePanel"));
        homePanel.add(createPurButton, gbc);

        gbc.gridx = 1; // X position 1
        JButton createDelButton = new JButton("Delivery from Supplier");
        createDelButton.addActionListener(e -> cardLayout.show(mainPanel, "createDeliveryPanel"));
        homePanel.add(createDelButton, gbc);

        gbc.gridx = 2; // X position 2
        JButton createRetButton = new JButton("Return Medicine to Supplier");
        createRetButton.addActionListener(e -> cardLayout.show(mainPanel, "createReturnPanel"));
        homePanel.add(createRetButton, gbc);

        // Row 2: Reports (Section 5.0)
        gbc.gridy = 2; // Y position 2 (third row)

        gbc.gridx = 0; // X position 0
        JButton purchReportButton = new JButton("Customer Purchase Report");
        purchReportButton.addActionListener(e -> cardLayout.show(mainPanel, "purchaseReportPanel"));
        homePanel.add(purchReportButton, gbc);

        gbc.gridx = 1; // X position 1
        JButton returnReportButton = new JButton("Medicine Return Report");
        returnReportButton.addActionListener(e -> cardLayout.show(mainPanel, "returnReportPanel"));
        homePanel.add(returnReportButton, gbc);

        gbc.gridx = 2; // X position 2
        JButton procReportButton = new JButton("Procurement Report");
        procReportButton.addActionListener(e -> cardLayout.show(mainPanel, "proReportPanel"));
        homePanel.add(procReportButton, gbc);

        // All Panels
        medicinePanel = new MedicinePanel(this);
        supplierPanel = new SupplierPanel(this);
        customerPanel = new CustomerPanel(this);
        createPurchasePanel = new CreatePurchasePanel(this);
        createDeliveryPanel = new CreateDeliveryPanel(this);
        createReturnPanel = new CreateReturnPanel(this);
        returnReportPanel = new MedicineReturnReportPanel(this);
        cpReportPanel = new CustomerPurchaseReportPanel(this);
        proReportPanel= new ProcurementReportPanel(this);
        

        // Add panels to main panel
        mainPanel.add(homePanel, "homePanel");
        mainPanel.add(medicinePanel, "medicinePanel");
        mainPanel.add(supplierPanel, "supplierPanel");
        mainPanel.add(customerPanel, "customerPanel");
        mainPanel.add(createPurchasePanel, "createPurchasePanel");
        mainPanel.add(createDeliveryPanel, "createDeliveryPanel");
        mainPanel.add(createReturnPanel, "createReturnPanel");
        mainPanel.add(returnReportPanel, "returnReportPanel");
        mainPanel.add(cpReportPanel, "purchaseReportPanel");
        mainPanel.add(proReportPanel, "proReportPanel");
        // .add() other panels here soon    

        add(mainPanel);
        cardLayout.show(mainPanel, "homePanel"); // show home first
    }

    public void goHome() {
        cardLayout.show(mainPanel, "homePanel");
    }
}
