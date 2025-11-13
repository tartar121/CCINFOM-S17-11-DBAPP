import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel homePanel;
    private MedicinePanel medicinePanel; // separate panel for medicines
    private SupplierPanel supplierPanel;
    private CustomerPanel customerPanel;
    private PurchasePanel purchasePanel;

    public MainView() {
        setTitle("Pharmacy Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // ===== Home Panel =====
        homePanel = new JPanel();
        homePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); // Create constraints object
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between buttons

        // Row 0: Records Management
        gbc.gridy = 0; // Y position 0 (top row)
        
        gbc.gridx = 0; // X position 0
        JButton medButton = new JButton("Medicine Management");
        medButton.addActionListener(e -> cardLayout.show(mainPanel, "medicinePanel"));
        homePanel.add(medButton, gbc); // add button with constraints
        
        gbc.gridx = 1; // X position 1
        JButton supButton = new JButton("Supplier Management");
        supButton.addActionListener(e -> cardLayout.show(mainPanel, "supplierPanel"));
        homePanel.add(supButton, gbc); // add button with constraints

        gbc.gridx = 2; // X position 2
        JButton cusButton = new JButton("Customer Management");
        cusButton.addActionListener(e -> cardLayout.show(mainPanel, "customerPanel"));
        homePanel.add(cusButton, gbc); // add button with constraints

        // Row 1: Transactions
        gbc.gridy = 1; // Y position 1 (second row)
        gbc.gridx = 0; // X position 0 (start of row from left)
        // gbc.gridwith = 3; // Span across 3 columns
        // gbc.fill = GridBagConstraints.HORIZONTAL; // Make button fill the space
        JButton purButton = new JButton("Purchase Transaction");
        purButton.addActionListener(e -> cardLayout.show(mainPanel, "purchasePanel"));
        homePanel.add(purButton, gbc); // add button with constraints

        // When add more transaction buttons, set gbc.gridx accordingly like gbc.gridx = 1, etc.

        // ===== Medicine Panel =====
        medicinePanel = new MedicinePanel(this);
        supplierPanel = new SupplierPanel(this);
        customerPanel = new CustomerPanel(this);
        purchasePanel = new PurchasePanel(this);

        // ===== Add panels to main panel =====
        mainPanel.add(homePanel, "homePanel");
        mainPanel.add(medicinePanel, "medicinePanel");
        mainPanel.add(supplierPanel, "supplierPanel");
        mainPanel.add(customerPanel, "customerPanel");
        mainPanel.add(purchasePanel, "purchasePanel");

        add(mainPanel);
        cardLayout.show(mainPanel, "homePanel"); // show home first
    }

    public void goHome() {
        cardLayout.show(mainPanel, "homePanel");
    }
}
