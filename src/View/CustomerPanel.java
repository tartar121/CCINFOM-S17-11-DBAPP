package View;

import Model.Customer;
import Controller.CustomerController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JPanel {
    private CustomerController cuscontroller;
    private JTable custable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, contactField, pwdField, statusField;

    public CustomerPanel(NewMainView mainView) {
        cuscontroller = new CustomerController();
        setLayout(new BorderLayout());

        // Top Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Add/View/Update Customer (cannot delete, if you want to delete change status to inactive)"));

        idField = new JTextField();
        nameField = new JTextField();
        contactField = new JTextField();
        pwdField = new JTextField();
        statusField = new JTextField();

        formPanel.add(new JLabel("ID (for updates)"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Contact info (email)"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Senior or PWD ID (blank if none)"));
        formPanel.add(pwdField);
        formPanel.add(new JLabel("Status (active/inactive)"));
        formPanel.add(statusField);

        add(formPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = new JButton("Add Customer");
        addButton.addActionListener(e -> addCustomer());
        JButton updateButton = new JButton("Update Customer");
        updateButton.addActionListener(e -> updateCustomer());
        JButton viewButton = new JButton("View Customer");
        viewButton.addActionListener(e -> viewCustomer());

        // Refresh Button 
        JButton refreshButton = new JButton("Refresh Table");
        refreshButton.addActionListener(e -> loadCustomers());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton); // Add refresh button to reload table data
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Name", "Contact info", "Senior or pwd id", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        custable = new JTable(tableModel);
        add(new JScrollPane(custable), BorderLayout.CENTER);

        loadCustomers();
    }

    private void addCustomer() {
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            int pwd;
            String pwdInput = pwdField.getText().trim();
            if (pwdInput.isEmpty()) {   
                pwd = 0;
            } else {
                pwd = Integer.parseInt(pwdInput); }
            String status = statusField.getText().trim();
            cuscontroller.addCustomer(new Customer(0, name, contact, pwd, status));
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearFields();
            loadCustomers();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }  
    }

    private void updateCustomer() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Customer current = cuscontroller.getCustomerbyId(id);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Customer ID not found.");
                return;
            }
    
            String name = nameField.getText().trim().isEmpty() ? current.getName() : nameField.getText().trim();
            String contact = contactField.getText().trim().isEmpty() ? current.getContactInfo() : contactField.getText().trim();
            int pwd = pwdField.getText().trim().isEmpty() ? current.getPwdId()
                    : Integer.parseInt(pwdField.getText().trim());
            String status = statusField.getText().trim().isEmpty() ? current.getStatus() : statusField.getText().trim();

            Customer updated= new Customer(id, name, contact, pwd, status);
            cuscontroller.updateCustomer(updated);
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            clearFields();
            loadCustomers();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }


    private void viewCustomer() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
    
            // Fetch customer from database
            Customer c = cuscontroller.getCustomerbyId(id);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Customer ID not found.");
                return;
            }
    
            // Display in fields
            nameField.setText(c.getName());
            contactField.setText(c.getContactInfo());
            pwdField.setText(String.valueOf(c.getPwdId()));
            statusField.setText(c.getStatus());
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            List<Customer> cus = cuscontroller.getAllCustomer();
            for (Customer c : cus) {
                Object pwdDisplay=(c.getPwdId()==0) ? "NULL":c.getPwdId();
                tableModel.addRow(new Object[]{
                        c.getId(), c.getName(), c.getContactInfo(), pwdDisplay, c.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading Customers: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        pwdField.setText("");
        statusField.setText("");
    }
}
