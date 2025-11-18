package View;

import Controller.CreateDeliveryController;
import Model.Medicine;
import Model.NewDeliveryItem; 
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
    private JButton addToDeliveryButton;

    // Delivery section
    private JTable deliveryTable;
    private DefaultTableModel deliveryTableModel;
    private JLabel totalLabel;

    // Actions
    private JButton completeDeliveryButton;

    // State
    private Supplier currentSupplier;
    private List<NewDeliveryItem> deliveryList;
    private double currentTotal = 0.0;

    public CreateDeliveryPanel(NewMainView mainView) {
        this.controller = new CreateDeliveryController();
        this.deliveryList = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Delivery of Medicine from Supplier"));
        
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
        
        // Middle Panel
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(new JLabel("Medicine ID:"));
        medicineIdField = new JTextField(8);
        middlePanel.add(medicineIdField);
        middlePanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        middlePanel.add(quantityField);
        addToDeliveryButton = new JButton("Add to Delivery List");
        middlePanel.add(addToDeliveryButton);
        
        // Add Top and Middle to a combined header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(middlePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Shopping deliveryList)
        String[] columns = {"Medicine ID", "Name", "Price", "Qty", "Total"};
        deliveryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deliveryTable = new JTable(deliveryTableModel);
        
        add(new JScrollPane(deliveryTable), BorderLayout.CENTER);

        // Bottom Panel (Total & Actions)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        totalLabel = new JLabel("Total: P0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonPanel.setBackground(Color.WHITE);
        completeDeliveryButton = new JButton("Complete Delivery");
        completeDeliveryButton.setEnabled(false); // Disabled until items are in list/rows
        
        actionButtonPanel.add(completeDeliveryButton);
        
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Disable controls until customer is set
        enableSaleControls(false);

        // Action Listeners
        findSupplierButton.addActionListener(e -> findCustomer());
        addToDeliveryButton.addActionListener(e -> addItemTodeliveryList());
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

    private void addItemTodeliveryList() {
        try {
            int medId = Integer.parseInt(medicineIdField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (qty <= 0) {
                throw new Exception("Quantity must be greater than 0.");
            }
            
            // Check if medicine is sellable and get its info
            Medicine medicine = controller.findMedicineBatch(medId);
            
            // Create deliveryListItem (it will auto-calculate discount)
            NewDeliveryItem item = new NewDeliveryItem(medicine, qty);
            
            // Add to deliveryList list and JTable
            deliveryList.add(item);
            deliveryTableModel.addRow(new Object[]{
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
            JOptionPane.showMessageDialog(this, "Medicine ID and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processDelivery() {
        if (deliveryList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Delivery list is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Complete delivery for " + currentSupplier.getName() + "?\nTotal: P" + String.format("%.2f", currentTotal),
            "Confirm Delivery", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Call the controller to run the SQL transaction
                int newDeliveryNo = controller.processDelivery(currentSupplier.getId(), deliveryList);;
                
                // This is the 'receipt'
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
        for (NewDeliveryItem item : deliveryList) {
            currentTotal += item.getTotal();
        }
        totalLabel.setText(String.format("Total: P%.2f", currentTotal));
    }
    
    private void enableSaleControls(boolean enabled) {
        medicineIdField.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        addToDeliveryButton.setEnabled(enabled);
        completeDeliveryButton.setEnabled(enabled);
    }
    
    private void clearForm() {
        supplierIdField.setText("");
        supplierStatusLabel.setText("Status: (Please find a customer)");
        supplierStatusLabel.setForeground(Color.BLUE);
        
        medicineIdField.setText("");
        quantityField.setText("");
        deliveryTableModel.setRowCount(0);
        deliveryList.clear();
        updateTotal();
        
        currentSupplier = null;
        enableSaleControls(false);
    }
}