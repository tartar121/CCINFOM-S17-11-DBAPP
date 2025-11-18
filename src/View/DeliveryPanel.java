package View;

import Model.Delivers;
import Model.DeliveryDetailsDisplay;
import Controller.DeliveryController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DeliveryPanel extends JPanel{
    private DeliveryController delcontroller;
    private JTable delTable;
    private DefaultTableModel tableModel;
    private JTextField dNoField, sIdField, reqDateField, shipDateField, statusField;
    private NewMainView mainView; 
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DeliveryPanel (NewMainView mainView) { 
        this.mainView = mainView;
        delcontroller = new DeliveryController();
        
        setBackground(Color.WHITE); 
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Delivery Records"));

        dNoField = new JTextField();
        sIdField = new JTextField();
        reqDateField = new JTextField();
        shipDateField = new JTextField();
        statusField = new JTextField();
        
        formPanel.add(new JLabel("Delivery No (for View/Update)"));
        formPanel.add(dNoField);
        formPanel.add(new JLabel("Supplier ID"));
        formPanel.add(sIdField);
        formPanel.add(new JLabel("Request Date (YYYY-MM-DD)"));
        formPanel.add(reqDateField);
        formPanel.add(new JLabel("Shipped Date (YYYY-MM-DD)"));
        formPanel.add(shipDateField);
        formPanel.add(new JLabel("Status (Delivered/Cancelled)"));
        formPanel.add(statusField);

        add(formPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        // JButton addButton = new JButton("Add Delivery");
        // addButton.addActionListener(e -> addDelivery());
        JButton updateButton = new JButton("Update Delivery");
        updateButton.addActionListener(e -> updateDelivery());
        JButton viewButton = new JButton("View Delivery");
        viewButton.addActionListener(e -> viewDelivery());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewDetails());

        JButton refreshButton = new JButton("Refresh Table");
        refreshButton.addActionListener(e -> loadDeliveries());
        
        // buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton); 
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"Delivery No", "Supplier ID", "Request Date", "Shipped Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        delTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(delTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        loadDeliveries();
    }
    
    private LocalDate parseDate(String text) throws DateTimeParseException {
        if (text == null || text.trim().isEmpty() || text.trim().equalsIgnoreCase("N/A")) {
            return null;
        }
        return LocalDate.parse(text.trim(), dateFormatter);
    }
    
    // Admin Add
    private void addDelivery() {
        try {
            int dNo = Integer.parseInt(dNoField.getText().trim());
            int supplierId = Integer.parseInt(sIdField.getText().trim());
            LocalDate reqDate = parseDate(reqDateField.getText());
            LocalDate shDate = parseDate(shipDateField.getText());
            String status = statusField.getText().trim();

            delcontroller.addDelivery(new Delivers(dNo, supplierId, reqDate, shDate, status));

            JOptionPane.showMessageDialog(this, "Delivery added successfully!");
            clearFields();
            loadDeliveries();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for IDs.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    
    /**
     * Admin Update
     * if the status is changed to 'Delivered', 
     * the stock of the medicines in that delivery are updated accordingly
     */
    private void updateDelivery() {
        try {
            int id = Integer.parseInt(dNoField.getText().trim());
            Delivers current = delcontroller.getDeliversByID(id);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Delivery ID not found.");
                return;
            }
    
            int supid = sIdField.getText().trim().isEmpty() ? current.getsid() : Integer.parseInt(sIdField.getText().trim());
            LocalDate sDate = parseDate(shipDateField.getText());
            LocalDate rDate = parseDate(reqDateField.getText());
            String status = statusField.getText().trim().isEmpty() ? current.getStatus() : statusField.getText().trim(); 
    
            Delivers updated = new Delivers(id, supid, rDate, sDate, status);
            delcontroller.updateDelivery(updated); // Calls the smart controller
    
            JOptionPane.showMessageDialog(this, "Delivery updated successfully!\nStock may have been updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadDeliveries();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void viewDelivery() {
        try {
            int id = Integer.parseInt(dNoField.getText().trim());
            Delivers d = delcontroller.getDeliversByID(id);
            if (d == null) {
                JOptionPane.showMessageDialog(this, "Delivery ID not found.");
                return;
            }
    
            sIdField.setText(String.valueOf(d.getsid()));
            reqDateField.setText(d.getrdate() != null ? d.getrdate().format(dateFormatter) : "N/A");
            shipDateField.setText(d.getsdate() != null ? d.getsdate().format(dateFormatter) : "N/A");
            statusField.setText(d.getStatus());
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void viewDetails() {
        int selectedRow = delTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a delivery from the table to view its details.", "No Delivery Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int deliveryNo = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            List<DeliveryDetailsDisplay> details = delcontroller.getDetailsForDelivery(deliveryNo);
            
            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No details found for Delivery No: " + deliveryNo, "No Details", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog detailsDialog = new JDialog(mainView, "Details for Delivery No: " + deliveryNo, true);
            detailsDialog.setSize(500, 300);
            detailsDialog.setLocationRelativeTo(this);
            
            String[] columns = {"Batch ID", "Medicine Name", "Qty Received", "Total Cost"};
            DefaultTableModel detailsModel = new DefaultTableModel(columns, 0);
            JTable detailsTable = new JTable(detailsModel);
            
            for (DeliveryDetailsDisplay item : details) {
                detailsModel.addRow(new Object[]{
                    item.getMedicineId(),
                    item.getMedicineName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getTotal())
                });
            }
            
            JScrollPane scrollPane = new JScrollPane(detailsTable);
            scrollPane.getViewport().setBackground(Color.WHITE);
            detailsDialog.add(scrollPane);
            detailsDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDeliveries() {
        tableModel.setRowCount(0); // clear existing rows
        try {
            List<Delivers> deliveries = delcontroller.getAllDeliveries();
            for (Delivers d : deliveries) {
                String requestDate = d.getrdate() != null ? d.getrdate().format(dateFormatter) : "N/A";
                String shippedDate = d.getsdate() != null ? d.getsdate().format(dateFormatter) : "N/A";
                tableModel.addRow(new Object[]{d.getdno(), d.getsid(), requestDate, shippedDate, d.getStatus()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading deliveries: " + e.getMessage());
        }
    }

    private void clearFields() {
        dNoField.setText("");
        sIdField.setText("");
        reqDateField.setText("");
        shipDateField.setText("");
        statusField.setText("");
    }
}