package View;

import Model.Medicine;
import Controller.MedicineController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MedicinePanel extends JPanel {
    private MedicineController medcontroller;
    private JTable medtable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, priceBoughtField, priceSaleField, qtyField, expDateField, disField;

    public MedicinePanel(NewMainView mainView) {
        medcontroller = new MedicineController();
        setLayout(new BorderLayout());

        // Top Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Add/View/Update Medicine (cannot delete, if you want to delete change discontinued to true)"));

        idField = new JTextField();
        nameField = new JTextField();
        priceBoughtField = new JTextField();
        priceSaleField = new JTextField();
        qtyField = new JTextField();
        expDateField = new JTextField();
        disField = new JTextField();

        formPanel.add(new JLabel("ID (for updates)"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Price Bought"));
        formPanel.add(priceBoughtField);
        formPanel.add(new JLabel("Price For Sale"));
        formPanel.add(priceSaleField);
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(qtyField);
        formPanel.add(new JLabel("Expiration (YYYY-MM-DD)"));
        formPanel.add(expDateField);
        formPanel.add(new JLabel("Discontinued (false/true)"));
        formPanel.add(disField);

        add(formPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = new JButton("Add Medicine");
        addButton.addActionListener(e -> addMedicine());
        JButton updateButton = new JButton("Update Medicine");
        updateButton.addActionListener(e -> updateMedicine());
        JButton viewButton = new JButton("View Medicine");
        viewButton.addActionListener(e -> viewMedicine());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Name", "Bought", "For Sale", "Qty", "Expiration", "Discontinued"};
        tableModel = new DefaultTableModel(columns, 0);
        medtable = new JTable(tableModel);
        add(new JScrollPane(medtable), BorderLayout.CENTER);

        loadMedicines();
    }

    private void addMedicine() {
        try {
            String name = nameField.getText().trim();
            double priceBought = Double.parseDouble(priceBoughtField.getText().trim());
            double priceForSale = Double.parseDouble(priceSaleField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());
            String expStr = expDateField.getText().trim();
            LocalDate expDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                expDate = LocalDate.parse(expStr, formatter);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid expiration date format. Use YYYY-MM-DD.");
                return;
            }
            String dis= disField.getText().trim().toLowerCase();
            boolean discontinued = dis.equals("true");
            medcontroller.addMedicine(new Medicine(0, name, priceBought, priceForSale, qty, expDate, discontinued));
            JOptionPane.showMessageDialog(this, "Medicine added successfully!");
            clearFields();
            loadMedicines();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID, prices, and quantity.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void updateMedicine() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Medicine current = medcontroller.getMedicineById(id);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Medicine ID not found.");
                return;
            }
    
            String name = nameField.getText().trim().isEmpty() ? current.getName() : nameField.getText().trim();
            double priceBought = priceBoughtField.getText().trim().isEmpty() ? current.getPriceBought()
                    : Double.parseDouble(priceBoughtField.getText().trim());
            double priceForSale = priceSaleField.getText().trim().isEmpty() ? current.getPriceForSale()
                    : Double.parseDouble(priceSaleField.getText().trim());
            int qty = qtyField.getText().trim().isEmpty() ? current.getQuantity()
                    : Integer.parseInt(qtyField.getText().trim());
            LocalDate expDate = expDateField.getText().trim().isEmpty() ? current.getExpirationDate()
                    : LocalDate.parse(expDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dis = disField.getText().trim().toLowerCase();
            boolean discontinued = dis.isEmpty() ? current.isDiscontinued() : dis.equals("true");
    
            Medicine updated = new Medicine(id, name, priceBought, priceForSale, qty, expDate, discontinued);
            medcontroller.updateMedicine(updated);
    
            JOptionPane.showMessageDialog(this, "Medicine updated successfully!");
            clearFields();
            loadMedicines();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }


    private void viewMedicine() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
    
            // Fetch medicine from database
            Medicine m = medcontroller.getMedicineById(id);
            if (m == null) {
                JOptionPane.showMessageDialog(this, "Medicine ID not found.");
                return;
            }
    
            // Display in fields
            nameField.setText(m.getName());
            priceBoughtField.setText(String.valueOf(m.getPriceBought()));
            priceSaleField.setText(String.valueOf(m.getPriceForSale()));
            qtyField.setText(String.valueOf(m.getQuantity()));
            expDateField.setText(m.getExpirationDate().toString());
            disField.setText(String.valueOf(m.isDiscontinued()));
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    public void loadMedicines() {
        tableModel.setRowCount(0);
        try {
            List<Medicine> meds = medcontroller.getAllMedicines();
            for (Medicine m : meds) {
                tableModel.addRow(new Object[]{
                        m.getId(), m.getName(), m.getPriceBought(), m.getPriceForSale(),
                        m.getQuantity(), m.getExpirationDate(), m.isDiscontinued()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading medicines: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceBoughtField.setText("");
        priceSaleField.setText("");
        qtyField.setText("");
        expDateField.setText("");
        disField.setText("");
    }
}
