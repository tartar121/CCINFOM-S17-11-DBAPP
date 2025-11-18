package View;

import Controller.CreateDeliveryController;
import Model.Medicine;
import Model.NewDeliveryItem; // <-- Uses our "Shopping List" class
import Model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CreateDeliveryPanel extends JPanel {
    private CreateDeliveryController controller;

    // Customer section
    private JTextField supplierIdField;
    private JButton findSupplierButton;
    private JLabel supplierStatusLabel;
    
    // Add item section
    private JTextField medicineIdField;
    private JTextField quantityField;
    private JButton addToCartButton;

    // Cart section
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;

    // Actions
    private JButton completeDeliveryButton;

    // State
    private Supplier currentSupplier;
    private List<NewDeliveryItem> cart;
    private double currentTotal = 0.0;

    public CreateDeliveryPanel(NewMainView mainView) {
        this.controller = new CreateDeliveryController();
        this.cart = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Top Panel (Customer) =====
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Delivery of Medicine (Point of Sale)"));
        
        JPanel supplierInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        supplierInputPanel.setBackground(Color.WHITE);
        supplierInputPanel.add(new JLabel("Enter Supplier ID:"));
        supplierIdField = new JTextField(10);
        supplierInputPanel.add(supplierIdField);
        findSupplierButton = new JButton("Find Supplier");
        supplierInputPanel.add(findSupplierButton);
        
        supplierStatusLabel = new JLabel("Status: (Please find a supplier)");
        supplierStatusLabel.setForeground(Color.BLUE);
        
        topPanel.add(supplierInputPanel, BorderLayout.NORTH);
        topPanel.add(supplierStatusLabel, BorderLayout.CENTER);
        
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
        String[] columns = {"Batch ID", "Name", "Price", "Qty", "Total"};
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
        completeDeliveryButton = new JButton("Complete Delivery");
        completeDeliveryButton.setEnabled(false); // Disabled until items are in cart
        
        actionButtonPanel.add(completeDeliveryButton);
        
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Disable controls until customer is set
        enableSaleControls(false);

        // Action Listeners
        findSupplierButton.addActionListener(e -> findCustomer());
        addToCartButton.addActionListener(e -> addItemToCart());
        completeDeliveryButton.addActionListener(e -> processDelivery());
    }

    private void findCustomer() {
        try {
            int supplierId = Integer.parseInt(supplierIdField.getText().trim());
            currentSupplier = controller.findSupplier(supplierId); // Checks if active
            
            
            supplierStatusLabel.setText("Supplier: " + currentSupplier.getName() + " | Status: ACTIVE");
            supplierStatusLabel.setForeground(new Color(0, 128, 0)); // Dark Green
            enableSaleControls(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Supplier ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            supplierStatusLabel.setText(e.getMessage());
            supplierStatusLabel.setForeground(Color.RED);
            enableSaleControls(false);
            currentSupplier = null;
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
            
            
            // 3. Create CartItem (it will auto-calculate discount)
            NewDeliveryItem item = new NewDeliveryItem(medicine, qty);
            
            // 4. Add to cart list and JTable
            cart.add(item);
            cartTableModel.addRow(new Object[]{
                item.getMedicineId(),
                item.getName(),
                item.getPriceBought(),
                item.getQuantity(),
                String.format("%.2f", item.getTotal())
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

    private void processDelivery() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shopping cart is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Complete delivery for " + currentSupplier.getName() + "?\nTotal: P" + String.format("%.2f", currentTotal),
            "Confirm Delivery", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Call the controller to run the SQL transaction
                int newDeliveryNo = controller.processDelivery(currentSupplier.getId(), cart);;
                
                // 2. This is your "Generated Receipt"
                JOptionPane.showMessageDialog(this, 
                    "Purchase processed successfully!\nReceipt Number: " + newDeliveryNo, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTotal() {
        currentTotal = 0.0;
        for (NewDeliveryItem item : cart) {
            currentTotal += item.getTotal();
        }
        totalLabel.setText(String.format("Total: P%.2f", currentTotal));
    }
    
    private void enableSaleControls(boolean enabled) {
        medicineIdField.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        addToCartButton.setEnabled(enabled);
        completeDeliveryButton.setEnabled(enabled);
    }
    
    private void clearForm() {
        supplierIdField.setText("");
        supplierStatusLabel.setText("Status: (Please find a customer)");
        supplierStatusLabel.setForeground(Color.BLUE);
        
        medicineIdField.setText("");
        quantityField.setText("");
        cartTableModel.setRowCount(0);
        cart.clear();
        updateTotal();
        
        currentSupplier = null;
        enableSaleControls(false);
    }
}