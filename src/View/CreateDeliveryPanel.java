package View;

import Controller.CreateDeliveryController;
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

    private JTextField supplierIdField;
    private JButton findSupplierButton;
    private JLabel supplierStatusLabel;
    private JTable newBatchTable;
    private DefaultTableModel tableModel;
    private JButton addRowButton;
    private JButton processDeliveryButton;
    private int currentSupplierId = -1;

    public CreateDeliveryPanel(NewMainView mainView) {
        this.controller = new CreateDeliveryController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Top Panel (Supplier Input) =====
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Receive New Delivery from Supplier"));
        
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
        
        add(topPanel, BorderLayout.NORTH);

        // Center Panel (New Batches Table)
        // This table is editable by the user
        String[] columns = {"Batch ID", "Name", "Price Bought", "Price For Sale", "Quantity", "Exp. Date (YYYY-MM-DD)"};
        tableModel = new DefaultTableModel(columns, 0);
        newBatchTable = new JTable(tableModel);
        
        add(new JScrollPane(newBatchTable), BorderLayout.CENTER);

        // ===== Bottom Panel (Actions) =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        JPanel tableButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRowButton = new JButton("Add New Row");
        tableButtons.add(addRowButton);

        JPanel mainButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainButtons.setBackground(Color.WHITE);
        processDeliveryButton = new JButton("Process Delivery");
        processDeliveryButton.setEnabled(false); // Disabled until supplier is found
        
        mainButtons.add(processDeliveryButton);
        
        bottomPanel.add(tableButtons, BorderLayout.WEST);
        bottomPanel.add(mainButtons, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Action Listeners =====
        findSupplierButton.addActionListener(e -> findSupplier());
        addRowButton.addActionListener(e -> addRow());
        processDeliveryButton.addActionListener(e -> processDelivery());
    }

    private void findSupplier() {
        try {
            int supplierId = Integer.parseInt(supplierIdField.getText().trim());
            // This calls the controller to check the business rule
            Supplier s = controller.findSupplier(supplierId); 
            
            supplierStatusLabel.setText("Supplier: " + s.getName() + " | Status: " + s.getStatus().toUpperCase());
            supplierStatusLabel.setForeground(new Color(0, 128, 0)); // Dark Green
            processDeliveryButton.setEnabled(true);
            currentSupplierId = s.getId();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Supplier ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            supplierStatusLabel.setText(e.getMessage());
            supplierStatusLabel.setForeground(Color.RED);
            processDeliveryButton.setEnabled(false);
            currentSupplierId = -1;
        }
    }

    private void addRow() {
        // Adds an empty, editable row to the table
        tableModel.addRow(new Object[]{"", "", "", "", "", ""});
    }

    private void processDelivery() {
        // 1. Create the "Shopping List" 📝 from the JTable
        List<NewDeliveryItem> itemsToDeliver = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int id = Integer.parseInt(tableModel.getValueAt(i, 0).toString().trim());
                String name = tableModel.getValueAt(i, 1).toString().trim();
                double pBought = Double.parseDouble(tableModel.getValueAt(i, 2).toString().trim());
                double pSale = Double.parseDouble(tableModel.getValueAt(i, 3).toString().trim());
                int qty = Integer.parseInt(tableModel.getValueAt(i, 4).toString().trim());
                LocalDate date = LocalDate.parse(tableModel.getValueAt(i, 5).toString().trim(), formatter);
                
                if (name.isEmpty()) {
                    throw new Exception("Medicine Name cannot be empty in row " + (i+1));
                }
                
                itemsToDeliver.add(new NewDeliveryItem(id, name, pBought, pSale, qty, date));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid data in table. Please check all rows.\nError: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (itemsToDeliver.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in delivery. Please add at least one row.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Confirm and process
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Receive " + itemsToDeliver.size() + " new batch(es) from Supplier " + currentSupplierId + "?",
            "Confirm Delivery", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 3. Call the controller to run the SQL transaction
                controller.processDelivery(currentSupplierId, itemsToDeliver);
                
                // 4. This is the "Generated Receipt"
                JOptionPane.showMessageDialog(this, "Delivery processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        supplierIdField.setText("");
        supplierStatusLabel.setText("Status: (Please find a supplier)");
        supplierStatusLabel.setForeground(Color.BLUE);
        tableModel.setRowCount(0);
        currentSupplierId = -1;
        processDeliveryButton.setEnabled(false);
    }
}