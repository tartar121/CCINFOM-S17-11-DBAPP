package View;

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

    // Transaction Record Panels (File Cabinet-kind)
    private PurchasePanel purchasePanel;
    private DeliveryPanel deliveryPanel;
    private ReturnPanel returnPanel;

    // Testing ReturnPanel
    private CreateReturnPanel createReturnPanel;

    // reports
    private ProcurementReportPanel proReportPanel;
    private CustomerPurchaseReportPanel cpReportPanel;

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

        // Row 0: Core Records
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

        // Row 1: Transaction Records
        gbc.gridy = 1; // Y position 1 (second row)
        gbc.gridx = 0; // X position 0
        JButton purButton = new JButton("Purchase Transactions");
        purButton.addActionListener(e -> cardLayout.show(mainPanel, "purchasePanel"));
        homePanel.add(purButton, gbc);

        gbc.gridx = 1; // X position 1
        JButton delButton = new JButton("Delivery Transactions");
        delButton.addActionListener(e -> cardLayout.show(mainPanel, "deliveryPanel"));
        homePanel.add(delButton, gbc);

        gbc.gridx = 2; // X position 2
        JButton retButton = new JButton("Return Transactions");
        retButton.addActionListener(e -> cardLayout.show(mainPanel, "returnPanel"));
        homePanel.add(retButton, gbc);

        // Row 2: Actual Transaction Panels (Test)
        gbc.gridy = 2; // Y position 2 (third row)
        gbc.gridx = 2; // X position 2
        JButton createRetButton = new JButton("Create New Return"); // <-- 3. ADD THE NEW BUTTON
        createRetButton.addActionListener(e -> cardLayout.show(mainPanel, "createReturnPanel"));
        homePanel.add(createRetButton, gbc);

        gbc.gridy= 2;
        gbc.gridx= 1;
        JButton proRepButton= new JButton("Generate Procurement Report");
        proRepButton.addActionListener(e-> cardLayout.show(mainPanel, "proReportPanel"));
        homePanel.add(proRepButton, gbc);

        gbc.gridy= 2;
        gbc.gridx= 0;
        JButton cpRepButton= new JButton("Generate Customer Purchase Report");
        cpRepButton.addActionListener(e-> cardLayout.show(mainPanel, "cpReportPanel"));
        homePanel.add(cpRepButton, gbc);
        // All Panels
        medicinePanel = new MedicinePanel(this);
        supplierPanel = new SupplierPanel(this);
        customerPanel = new CustomerPanel(this);
        purchasePanel = new PurchasePanel(this, medicinePanel);
        deliveryPanel = new DeliveryPanel(this);
        returnPanel = new ReturnPanel(this);
        proReportPanel= new ProcurementReportPanel(this);
        cpReportPanel = new CustomerPurchaseReportPanel(this);

        // panel for testing CreateReturnPanel
        createReturnPanel = new CreateReturnPanel(this);

        // Add panels to main panel
        mainPanel.add(homePanel, "homePanel");
        mainPanel.add(medicinePanel, "medicinePanel");
        mainPanel.add(supplierPanel, "supplierPanel");
        mainPanel.add(customerPanel, "customerPanel");
        mainPanel.add(purchasePanel, "purchasePanel");
        mainPanel.add(deliveryPanel, "deliveryPanel");
        mainPanel.add(returnPanel, "returnPanel");
        

        // add CreateReturnPanel to main panel
        mainPanel.add(createReturnPanel, "createReturnPanel");
        mainPanel.add(proReportPanel, "proReportPanel");
        mainPanel.add(cpReportPanel, "cpReportPanel");

        add(mainPanel);
        cardLayout.show(mainPanel, "homePanel"); // show home first
    }

    public void goHome() {
        cardLayout.show(mainPanel, "homePanel");
    }
}
