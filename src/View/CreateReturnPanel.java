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
    private MainView mainView;

    private JTextField supplierIdField;
    private JButton findButton;
    private JTable returnableItemsTable;
    private DefaultTableModel tableModel;
    private JButton processReturnButton;
    private List<ReturnableItem> currentItems; // To hold the items from the search

    public CreateReturnPanel(MainView mainView) {
        this.mainView = mainView;
        this.controller = new CreateReturnController();
        this.currentItems = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Top Panel (Supplier Input) =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Return Expired/Discontinued Medicine to Supplier"));
        
        topPanel.add(new JLabel("Enter Supplier ID:"));
        supplierIdField = new JTextField(10);
        topPanel.add(supplierIdField);
        findButton = new JButton("Find Returnable Items");
        topPanel.add(findButton);
        
        add(topPanel, BorderLayout.NORTH);

        // ===== Center Panel (Items Table) =====
        String[] columns = {"Batch ID", "Name", "Exp. Date", "Qty", "Delivery No", "Shipped Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            // Make table cells not editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        returnableItemsTable = new JTable(tableModel);
        
        add(new JScrollPane(returnableItemsTable), BorderLayout.CENTER);

        // ===== Bottom Panel (Actions) =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        processReturnButton = new JButton("Process Return");
        processReturnButton.setEnabled(false); // Disabled until items are found
        
        JButton homeButton = new JButton("Back to Home");
        
        bottomPanel.add(processReturnButton);
        bottomPanel.add(homeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Action Listeners =====
        findButton.addActionListener(e -> findItems());
        processReturnButton.addActionListener(e -> processReturn());
        homeButton.addActionListener(e -> {
            clearForm();
            mainView.goHome();
        });
    }

    private void findItems() {
        try {
            int supplierId = Integer.parseInt(supplierIdField.getText().trim());
            // 1. Call the controller to get items
            currentItems = controller.findReturnableItems(supplierId);
            
            // 2. Clear the table
            tableModel.setRowCount(0);
            
            // 3. Populate the table with returnable items
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
            
            // 4. Enable the return button
            processReturnButton.setEnabled(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Supplier ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            clearForm();
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
                
                // 2. This is your "Generating a receipt"
                JOptionPane.showMessageDialog(this, "Return processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        supplierIdField.setText("");
        tableModel.setRowCount(0);
        currentItems.clear();
        processReturnButton.setEnabled(false);
    }
}