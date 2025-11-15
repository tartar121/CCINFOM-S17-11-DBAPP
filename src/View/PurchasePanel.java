package View;

import Model.Purchase;
import Model.PurchaseDetails;
import Model.Customer;
import Model.Medicine;
import Controller.CustomerController;
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
    private CustomerController cuscontroller;
    private MedicineController medcontroller;
    private JTable purtable;
    private DefaultTableModel tableModel;
    private JTextField pNoField, purDateField, cIdField, mIdField, qtyField, discountField, totalField;
    private MainView mainView;

    public PurchasePanel(MainView mainView) {
        this.mainView = mainView;
        purcontroller = new PurchaseController();
        cuscontroller = new CustomerController();
        medcontroller = new MedicineController();
        setLayout(new BorderLayout());

        // ===== Top Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        pNoField = new JTextField();
        purDateField = new JTextField();
        cIdField = new JTextField();
        mIdField = new JTextField();
        qtyField = new JTextField();
        discountField = new JTextField();
        discountField.setEditable(false); 
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

        generatePurchaseNo();
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
            double total = Double.parseDouble(totalField.getText().trim());
            purcontroller.addPurchase(new Purchase(pNo, purDate, cId), new PurchaseDetails(pNo, mId, qty, dis, total));
            JOptionPane.showMessageDialog(this, "Purchase added successfully!");
            clearFields();
            generatePurchaseNo();
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
            PurchaseDetails currentPD = purcontroller.getPurchaseDetailsByPurchaseNo(pNo).get(0);
            int mId = mIdField.getText().trim().isEmpty() ? currentPD.getMedicineId()
                : Integer.parseInt(mIdField.getText().trim());
            int qty = qtyField.getText().trim().isEmpty() ? currentPD.getQuantityOrder()
                : Integer.parseInt(qtyField.getText().trim());
            Double discount = discountField.getText().trim().isEmpty() ? currentPD.getDiscount()
                : Double.parseDouble(discountField.getText().trim());
            double total = totalField.getText().trim().isEmpty() ? currentPD.getTotal()
                : Double.parseDouble(totalField.getText().trim());


            Medicine med = medcontroller.getMedicineById(mId);
            if (med == null) {
                JOptionPane.showMessageDialog(this, "Medicine not found.");
                return;
            }
            /*if (med.isDiscontinued()) {
                JOptionPane.showMessageDialog(this, "Medicine has been discontinued.");
                return;
            } */
            if (med.getExpirationDate() != null && med.getExpirationDate().isBefore(purDate)) {
                JOptionPane.showMessageDialog(this, "Medicine has expired.");
                return;
            }
            Purchase updatedP = new Purchase(pNo, purDate, cId);
            PurchaseDetails updatedPD = new PurchaseDetails(pNo, mId, qty, discount, total);
            purcontroller.updatePurchase(updatedP);
            purcontroller.updatePurchaseDetails(updatedPD);
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
            Medicine m = medcontroller.getMedicine(pNo);
            List<PurchaseDetails> details = purcontroller.getPurchaseDetailsByPurchaseNo(pNo);

            // Display in fields
            purDateField.setText(p.getPurchaseDate().toString());
            cIdField.setText(String.valueOf(p.getCustomerId()));
            mIdField.setText(String.valueOf(m.getId()));
            for (PurchaseDetails pd : details) {
                qtyField.setText(String.valueOf(pd.getQuantityOrder()));
                discountField.setText(String.valueOf(pd.getDiscount()));
                totalField.setText(String.valueOf(pd.getTotal()));
            }

            
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
                for (PurchaseDetails pd : details) {
                    Object discountValue = (pd.getDiscount() == null || pd.getDiscount() == 0) ? "NULL" : pd.getDiscount();
                    tableModel.addRow(new Object[]{
                        p.getPurchaseNo(),
                        p.getPurchaseDate(),
                        p.getCustomerId(),
                        pd.getMedicineId(),
                        pd.getQuantityOrder(),
                        discountValue,
                        pd.getTotal()
                        });
                    }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading purchases: " + e.getMessage());
        }
    }

    private void clearFields() {
        purDateField.setText("");
        cIdField.setText("");
        mIdField.setText(""); 
        qtyField.setText(""); 
        discountField.setText(""); 
        totalField.setText("");
    }
    private void calculateTotal() {
        try {
            String qtyText = qtyField.getText().trim();
            String mIdText = mIdField.getText().trim();
            String cIdText = cIdField.getText().trim();

            if (qtyText.isEmpty() || mIdText.isEmpty()) return;

            int qty = Integer.parseInt(qtyText);
            int mid = Integer.parseInt(mIdText);

            Medicine m = medcontroller.getMedicineById(mid);
            if (m == null) return;

            double price = m.getPriceForSale();
            double discount = 0.0;

            if (!cIdText.isEmpty()) {
                int cId = Integer.parseInt(cIdText);
                Customer c = cuscontroller.getCustomerbyId(cId);
                if (c != null && c.getPwdId() != 0) {
                    discount = 0.20; 
                }
            }   
            double total = price * qty * (1 - discount);
            totalField.setText(String.format("%.2f", total));
    } catch (Exception e) {
        totalField.setText("");
    }
}

    private void generatePurchaseNo() {
        try {
            int next = new PurchaseController().getNextPurchaseNo();
            pNoField.setText(String.valueOf(next));
        } catch (SQLException e){
            pNoField.setText("1");
        }
    }
}
