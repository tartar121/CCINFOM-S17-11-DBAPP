package View;

import Controller.CreateReturnController;
import Model.ReturnableItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateReturnPanel extends JPanel {
    private CreateReturnController controller;

    private JTextField supplierIdField;
    private JButton findButton;
    private JLabel supplierStatusLabel;
    private JTable returnableItemsTable;
    private DefaultTableModel tableModel;
    private JButton processReturnButton;
    private List<ReturnableItem> currentItems; // To hold the items from the search

    public CreateReturnPanel(NewMainView mainView) {
        this.controller = new CreateReturnController();
        this.currentItems = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 5)); 
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Return Expired/Discontinued Medicine to Supplier"));
        
        JPanel supplierInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        supplierInputPanel.setBackground(Color.WHITE);
        supplierInputPanel.add(new JLabel("Enter Supplier ID:"));
        supplierIdField = new JTextField(10);
        supplierInputPanel.add(supplierIdField);
        findButton = new JButton("Find Returnable Items");
        supplierInputPanel.add(findButton);
        
        supplierStatusLabel = new JLabel("Status: (Please find a supplier)"); 
        supplierStatusLabel.setForeground(Color.BLUE);
        
        topPanel.add(supplierInputPanel, BorderLayout.NORTH);
        topPanel.add(supplierStatusLabel, BorderLayout.CENTER); 
        
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        String[] columns = {"Batch ID", "Name", "Exp. Date", "Qty", "Delivery No", "Shipped Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        returnableItemsTable = new JTable(tableModel);
        
        add(new JScrollPane(returnableItemsTable), BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        processReturnButton = new JButton("Process Return");
        processReturnButton.setEnabled(false); // Disabled until items are found
        
        bottomPanel.add(processReturnButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        findButton.addActionListener(e -> findItems());
        processReturnButton.addActionListener(e -> processReturn());
    }

    private void findItems() {
        // This method now does two things:
        // 1. Find and validate the supplier
        // 2. Find the returnable items
        try {
            int supplierId = Integer.parseInt(supplierIdField.getText().trim());

            // 1. Call the controller to get items
            // This method *also* checks if the supplier is active
            currentItems = controller.findReturnableItems(supplierId);
            
            // 2. If successful, update the status label
            supplierStatusLabel.setText("Supplier (ID: " + supplierId + ") is ACTIVE and has returnable items.");
            supplierStatusLabel.setForeground(new Color(0, 128, 0)); // Dark Green
            
            // 3. Clear the table
            tableModel.setRowCount(0);
            
            // 4. Populate the table with returnable items
            for (ReturnableItem item : currentItems) {
                tableModel.addRow(new Object[]{
                    item.getMedicineId(),
                    item.getMedicineName(),
                    item.getExpirationDate(),
                    item.getQuantity(),
                    item.getDeliveryNo(),
                    item.getShippedDate()
                });
            }
            
            // 5. Enable the return button
            processReturnButton.setEnabled(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Supplier ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            supplierStatusLabel.setText("Status: Invalid Supplier ID.");
            supplierStatusLabel.setForeground(Color.RED);
            clearForm(); // Clear just the table/button
        } catch (SQLException e) {
            // This will catch "No returnable items found" OR "Supplier is inactive"
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            supplierStatusLabel.setText("Status: " + e.getMessage());
            supplierStatusLabel.setForeground(Color.RED);
            clearForm(); // Clear just the table/button
        }
    }

    private void processReturn() {
        if (currentItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items found to return.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int supplierId;
        try {
             supplierId = Integer.parseInt(supplierIdField.getText().trim());
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Supplier ID is no longer valid. Please search again.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "You are about to return " + currentItems.size() + " batch(es) to Supplier " + supplierId + ".\n" +
            "This action is permanent and will set their stock to 0.\nContinue?",
            "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Call the controller to run the SQL transaction
                controller.processReturn(supplierId, currentItems);
                
                // 2. This is your "Generated Receipt"
                JOptionPane.showMessageDialog(this, "Return processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormAndSupplier(); // Clear everything
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Clears only the table and buttons, keeps the supplier info.
     */
    private void clearForm() {
        tableModel.setRowCount(0);
        currentItems.clear();
        processReturnButton.setEnabled(false);
    }
    
    /**
     * Clears the entire form, including supplier info.
     */
    private void clearFormAndSupplier() {
        supplierIdField.setText("");
        supplierStatusLabel.setText("Status: (Please find a supplier)");
        supplierStatusLabel.setForeground(Color.BLUE);
        tableModel.setRowCount(0);
        currentItems.clear();
        processReturnButton.setEnabled(false);
    }
}