package View;

import Model.Supplier;
import Controller.SupplierController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SupplierPanel extends JPanel {
    private SupplierController supcontroller;
    private JTable suptable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, addressField, contactField, statusField;
    private MainView mainView;

    public SupplierPanel(MainView mainView) {
        this.mainView = mainView;
        supcontroller = new SupplierController();
        setLayout(new BorderLayout());

        // ===== Top Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add/View/Update Supplier (cannot delete, if you want to delete change status to inactive)"));

        idField = new JTextField();
        nameField = new JTextField();
        addressField = new JTextField();
        contactField = new JTextField();
        statusField = new JTextField();

        formPanel.add(new JLabel("ID (for updates)"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Address"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Contact info (email)"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Status (active/inactive)"));
        formPanel.add(statusField);

        add(formPanel, BorderLayout.NORTH);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Supplier");
        addButton.addActionListener(e -> addSupplier());
        JButton updateButton = new JButton("Update Supplier");
        updateButton.addActionListener(e -> updateSupplier());
        JButton viewButton = new JButton("View Supplier");
        viewButton.addActionListener(e -> viewSupplier());
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> mainView.goHome());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Table =====
        String[] columns = {"ID", "Name", "Address", "Contact info", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        suptable = new JTable(tableModel);
        add(new JScrollPane(suptable), BorderLayout.CENTER);

        loadSuppliers();
    }

    private void addSupplier() {
        try {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String contact = contactField.getText().trim();
            String status = statusField.getText().trim();
            supcontroller.addSupplier(new Supplier(0, name, address, contact, status));
            JOptionPane.showMessageDialog(this, "Supplier added successfully!");
            clearFields();
            loadSuppliers();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void updateSupplier() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Supplier current = supcontroller.getSupplierbyId(id);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Supplier ID not found.");
                return;
            }
    
            String name = nameField.getText().trim().isEmpty() ? current.getName() : nameField.getText().trim();
            String address = addressField.getText().trim().isEmpty() ? current.getAddress() : addressField.getText().trim();
            String contact = contactField.getText().trim().isEmpty() ? current.getContactInfo() : contactField.getText().trim();
            String status = statusField.getText().trim().isEmpty() ? current.getStatus() : statusField.getText().trim();

            Supplier updated= new Supplier(id, name, address, contact, status);
            supcontroller.updateSupplier(updated);
            JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
            clearFields();
            loadSuppliers();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }


    private void viewSupplier() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
    
            // Fetch medicine from database
            Supplier s = supcontroller.getSupplierbyId(id);
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Supplier ID not found.");
                return;
            }
    
            // Display in fields
            nameField.setText(s.getName());
            addressField.setText(s.getAddress());
            contactField.setText(s.getContactInfo());
            statusField.setText(s.getStatus());
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void loadSuppliers() {
        tableModel.setRowCount(0);
        try {
            List<Supplier> supply = supcontroller.getAllSupplier();
            for (Supplier s : supply) {
                tableModel.addRow(new Object[]{
                        s.getId(), s.getName(), s.getAddress(), s.getContactInfo(), s.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        contactField.setText("");
        statusField.setText("");
    }
}

