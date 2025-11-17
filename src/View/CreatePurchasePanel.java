package View;

import Controller.CreatePurchaseController;
import Model.CartItem;
import Model.Customer;
import Model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreatePurchasePanel extends JPanel {
    private CreatePurchaseController controller;

    // Customer section
    private JTextField customerIdField;
    private JButton findCustomerButton;
    private JLabel customerStatusLabel;
    
    // Add item section
    private JTextField medicineIdField;
    private JTextField quantityField;
    private JButton addToCartButton;

    // Cart section
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;

    // Actions
    private JButton completePurchaseButton;

    // State
    private Customer currentCustomer;
    private List<CartItem> cart;
    private double currentTotal = 0.0;

    public CreatePurchasePanel(NewMainView mainView) {
        this.controller = new CreatePurchaseController();
        this.cart = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Top Panel (Customer) =====
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Purchase a Medicine (Point of Sale)"));
        
        JPanel customerInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerInputPanel.setBackground(Color.WHITE);
        customerInputPanel.add(new JLabel("Enter Customer ID:"));
        customerIdField = new JTextField(10);
        customerInputPanel.add(customerIdField);
        findCustomerButton = new JButton("Find Customer");
        customerInputPanel.add(findCustomerButton);
        
        customerStatusLabel = new JLabel("Status: (Please find a customer)");
        customerStatusLabel.setForeground(Color.BLUE);
        
        topPanel.add(customerInputPanel, BorderLayout.NORTH);
        topPanel.add(customerStatusLabel, BorderLayout.CENTER);
        
        // Middle Panel (Add to Cart)
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(new JLabel("Batch ID:"));
        medicineIdField = new JTextField(8);
        middlePanel.add(medicineIdField);
        middlePanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        middlePanel.add(quantityField);
        addToCartButton = new JButton("Add to Cart");
        middlePanel.add(addToCartButton);
        
        // Add Top and Middle to a combined header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(middlePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Shopping Cart)
        String[] columns = {"Batch ID", "Name", "Price", "Qty", "Discount", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Bottom Panel (Total & Actions)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        totalLabel = new JLabel("Total: P0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonPanel.setBackground(Color.WHITE);
        completePurchaseButton = new JButton("Complete Purchase");
        completePurchaseButton.setEnabled(false); // Disabled until items are in cart
        
        actionButtonPanel.add(completePurchaseButton);
        
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Disable controls until customer is set
        enableSaleControls(false);

        // Action Listeners
        findCustomerButton.addActionListener(e -> findCustomer());
        addToCartButton.addActionListener(e -> addItemToCart());
        completePurchaseButton.addActionListener(e -> processPurchase());
    }

    private void findCustomer() {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            currentCustomer = controller.findCustomer(customerId); // Checks if active
            
            String discountInfo = "";
            if (currentCustomer.getPwdId() != 0) { // Check if PWD/Senior
                discountInfo = " | SENIOR/PWD (20% Discount)";
            }
            
            customerStatusLabel.setText("Customer: " + currentCustomer.getName() + " | Status: ACTIVE" + discountInfo);
            customerStatusLabel.setForeground(new Color(0, 128, 0)); // Dark Green
            enableSaleControls(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Customer ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            customerStatusLabel.setText(e.getMessage());
            customerStatusLabel.setForeground(Color.RED);
            enableSaleControls(false);
            currentCustomer = null;
        }
    }

    private void addItemToCart() {
        try {
            int medId = Integer.parseInt(medicineIdField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (qty <= 0) {
                throw new Exception("Quantity must be greater than 0.");
            }
            
            // 1. Check if medicine is sellable and get its info
            Medicine medicine = controller.findMedicineBatch(medId);
            
            // 2. Check if we have enough stock
            if (qty > medicine.getQuantity()) {
                throw new SQLException("Not enough stock. Only " + medicine.getQuantity() + " available.");
            }
            
            // 3. Create CartItem (it will auto-calculate discount)
            boolean applyDiscount = (currentCustomer.getPwdId() != 0);
            CartItem item = new CartItem(medicine, qty, applyDiscount);
            
            // 4. Add to cart list and JTable
            cart.add(item);
            cartTableModel.addRow(new Object[]{
                item.getMedicineId(),
                item.getMedicineName(),
                String.format("%.2f", item.getPriceForSale()),
                item.getQuantityOrdered(),
                String.format("%.2f", item.getDiscount()),
                String.format("%.2f", item.getLineTotal())
            });
            
            updateTotal();
            medicineIdField.setText("");
            quantityField.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Batch ID and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processPurchase() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shopping cart is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Complete purchase for " + currentCustomer.getName() + "?\nTotal: P" + String.format("%.2f", currentTotal),
            "Confirm Purchase", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Call the controller to run the SQL transaction
                int newPurchaseNo = controller.processPurchase(currentCustomer.getId(), cart);
                
                // 2. This is your "Generated Receipt"
                JOptionPane.showMessageDialog(this, 
                    "Purchase processed successfully!\nReceipt Number: " + newPurchaseNo, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTotal() {
        currentTotal = 0.0;
        for (CartItem item : cart) {
            currentTotal += item.getLineTotal();
        }
        totalLabel.setText(String.format("Total: P%.2f", currentTotal));
    }
    
    private void enableSaleControls(boolean enabled) {
        medicineIdField.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        addToCartButton.setEnabled(enabled);
        completePurchaseButton.setEnabled(enabled);
    }
    
    private void clearForm() {
        customerIdField.setText("");
        customerStatusLabel.setText("Status: (Please find a customer)");
        customerStatusLabel.setForeground(Color.BLUE);
        
        medicineIdField.setText("");
        quantityField.setText("");
        cartTableModel.setRowCount(0);
        cart.clear();
        updateTotal();
        
        currentCustomer = null;
        enableSaleControls(false);
    }
}