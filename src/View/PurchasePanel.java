package View;

import Model.Purchase;
import Model.PurchaseDetails;
import Controller.MedicineController;
import Controller.PurchaseController;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


public class PurchasePanel extends JPanel {
    private PurchaseController purcontroller;
    private JTable purtable;
    private DefaultTableModel tableModel;
    private JTextField pNoField, purDateField, cIdField, mIdField, qtyField, discountField, totalField;
    private MainView mainView;

    public PurchasePanel(MainView mainView) {
        this.mainView = mainView;
        purcontroller = new PurchaseController();
        setLayout(new BorderLayout());

        // ===== Top Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        pNoField = new JTextField();
        purDateField = new JTextField();
        cIdField = new JTextField();
        mIdField = new JTextField();
        qtyField = new JTextField();
        discountField = new JTextField();
        totalField = new JTextField();
        totalField.setEditable(false); 

        formPanel.add(new JLabel("Purchase No"));
        formPanel.add(pNoField);
        formPanel.add(new JLabel("Purchase Date (YYYY-MM-DD)"));
        formPanel.add(purDateField);
        formPanel.add(new JLabel("Customer ID"));
        formPanel.add(cIdField);
        formPanel.add(new JLabel("Medicine ID"));
        formPanel.add(mIdField);
        formPanel.add(new JLabel("Purchase Quantity"));
        formPanel.add(qtyField);
        formPanel.add(new JLabel("Discount"));
        formPanel.add(discountField);
        formPanel.add(new JLabel("Total"));
        formPanel.add(totalField);

        add(formPanel, BorderLayout.NORTH);
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateTotal(); }
            public void removeUpdate(DocumentEvent e) { calculateTotal(); }
            public void changedUpdate(DocumentEvent e) { calculateTotal(); }
        }; 
        qtyField.getDocument().addDocumentListener(docListener);
        mIdField.getDocument().addDocumentListener(docListener);
        discountField.getDocument().addDocumentListener(docListener);
        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Purchase");
        addButton.addActionListener(e -> addPurchase());
        JButton updateButton = new JButton("Update Purchase");
        updateButton.addActionListener(e -> updatePurchase());
        JButton viewButton = new JButton("View Purchase");
        viewButton.addActionListener(e -> viewPurchase());
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> mainView.goHome());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Table =====
        String[] columns = {"Purchase No", "Purchase Date", "Customer ID", "Medicine ID", "Purchase Quantity", "Discount", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        purtable = new JTable(tableModel);
        add(new JScrollPane(purtable), BorderLayout.CENTER);

        loadPurchases();
    }

    private void addPurchase() {
        try {
            int pNo = Integer.parseInt(pNoField.getText().trim());
            String purStr = purDateField.getText().trim();
            LocalDate purDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                purDate = LocalDate.parse(purStr, formatter);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid purchase date format. Use YYYY-MM-DD.");
                return;
            }
            int cId = Integer.parseInt(cIdField.getText().trim());
            int mId = Integer.parseInt(mIdField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());
            String disText = discountField.getText().trim();
            Double dis = null;
            if (!disText.isEmpty()) {
                dis = Double.parseDouble(disText);
            }
            double total = Float.parseFloat(totalField.getText().trim());
            purcontroller.addPurchase(new Purchase(pNo, purDate, cId), new PurchaseDetails(pNo, mId, qty, dis, total));
            JOptionPane.showMessageDialog(this, "Purchase added successfully!");
            clearFields();
            loadPurchases();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void updatePurchase() {
        try {
            int pNo = Integer.parseInt(pNoField.getText().trim());
            Purchase current = purcontroller.getPurchaseByNo(pNo);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Purchase No not found.");
                return;
            }

            LocalDate purDate = purDateField.getText().trim().isEmpty() ? current.getPurchaseDate()
                    : LocalDate.parse(purDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int cId = cIdField.getText().trim().isEmpty() ? current.getCustomerId()
                    : Integer.parseInt(cIdField.getText().trim());
            
    
            Purchase updated = new Purchase(pNo, purDate, cId);
            purcontroller.updatePurchase(updated);
    
            JOptionPane.showMessageDialog(this, "Purchase updated successfully!");
            clearFields();
            loadPurchases();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }


    private void viewPurchase() {
        try {
            int pNo = Integer.parseInt(pNoField.getText().trim());
    
            // Fetch purchase from database
            Purchase p = purcontroller.getPurchaseByNo(pNo);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Purchase No not found.");
                return;
            }
    
            // Display in fields
            purDateField.setText(p.getPurchaseDate().toString());
            cIdField.setText(String.valueOf(p.getCustomerId()));
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void loadPurchases() {
        tableModel.setRowCount(0);
        try {
            List<Purchase> pur = purcontroller.getAllPurchases();
            for (Purchase p : pur) {
                List<PurchaseDetails> details = purcontroller.getPurchaseDetailsByPurchaseNo(p.getPurchaseNo());
                if (details.isEmpty()) {
                    tableModel.addRow(new Object[]{
                        p.getPurchaseNo(),
                        p.getPurchaseDate(),
                        p.getCustomerId(),
                        "", "", "", ""
                    });
                } else {
                    for (PurchaseDetails pd : details) {
                        Object discount = pd.getDiscount() < 0 ? "NULL" : pd.getDiscount();
                        tableModel.addRow(new Object[]{
                            p.getPurchaseNo(),
                            p.getPurchaseDate(),
                            p.getCustomerId(),
                            pd.getMedicineId(),
                            pd.getQuantityOrder(),
                            pd.getDiscount(),
                            pd.getTotal()
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading purchases: " + e.getMessage());
        }
    }

    private void clearFields() {
        pNoField.setText("");
        purDateField.setText("");
        cIdField.setText("");
        mIdField.setText(""); 
        qtyField.setText(""); 
        discountField.setText(""); 
        totalField.setText("");
    }
    private void calculateTotal() {
        try {
            int mId = Integer.parseInt(mIdField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());
            double discount = 0;
            String disText = discountField.getText().trim();
            if (!disText.isEmpty()) {
                discount = Double.parseDouble(disText);
            }
            double price = new MedicineController().getSalePrice(mId);
            double total = price*qty*(1-discount/100);
        
            totalField.setText(String.format("%.2f", total));
        } catch (NumberFormatException | SQLException e) {
            totalField.setText("0.00"); 
        }
    }
}
