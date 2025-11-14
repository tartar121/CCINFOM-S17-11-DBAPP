import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel homePanel;
    private MedicinePanel medicinePanel; // separate panel for medicines
    private SupplierPanel supplierPanel;
    private CustomerPanel customerPanel;
    private DeliveryPanel deliveryPanel;

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
        JButton medButton = new JButton("Medicine Management");
        medButton.addActionListener(e -> cardLayout.show(mainPanel, "medicinePanel"));
        JButton supButton = new JButton("Supplier Management");
        supButton.addActionListener(e -> cardLayout.show(mainPanel, "supplierPanel"));
        JButton cusButton = new JButton("Customer Management");
        cusButton.addActionListener(e -> cardLayout.show(mainPanel, "customerPanel"));
        JButton delButton = new JButton("Delivery Transactions");
        delButton.addActionListener(e -> cardLayout.show(mainPanel, "deliveryPanel"));

        homePanel.add(medButton);
        homePanel.add(supButton);
        homePanel.add(cusButton);
        homePanel.add(delButton);

        // ===== Medicine Panel =====
        medicinePanel = new MedicinePanel(this);
        supplierPanel = new SupplierPanel(this);
        customerPanel = new CustomerPanel(this);
        deliveryPanel = new DeliveryPanel(this);

        // ===== Add panels to main panel =====
        mainPanel.add(homePanel, "homePanel");
        mainPanel.add(medicinePanel, "medicinePanel");
        mainPanel.add(supplierPanel, "supplierPanel");
        mainPanel.add(customerPanel, "customerPanel");
        mainPanel.add(deliveryPanel, "deliveryPanel");

        add(mainPanel);
        cardLayout.show(mainPanel, "homePanel"); // show home first
    }

    public void goHome() {
        cardLayout.show(mainPanel, "homePanel");
    }
}
