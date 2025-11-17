package View;

import Model.Purchase;
import Model.PurchaseDetailsDisplay;
import Controller.PurchaseController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PurchasePanel extends JPanel {
    private PurchaseController purcontroller;
    private JTable purtable;
    private DefaultTableModel tableModel;
    private JTextField pNoField, purDateField, cIdField;
    private NewMainView mainView; 
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PurchasePanel(NewMainView mainView) {
        this.mainView = mainView;
        purcontroller = new PurchaseController();
        
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBackground(Color.WHITE); 
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Purchase Records"));

        pNoField = new JTextField();
        purDateField = new JTextField();
        cIdField = new JTextField();

        formPanel.add(new JLabel("Purchase No (for View/Update)"));
        formPanel.add(pNoField);
        formPanel.add(new JLabel("Purchase Date (YYYY-MM-DD)"));
        formPanel.add(purDateField);
        formPanel.add(new JLabel("Customer ID"));
        formPanel.add(cIdField);

        add(formPanel, BorderLayout.NORTH);
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE); 
        JButton addButton = new JButton("Add Purchase");
        addButton.addActionListener(e -> addPurchase());
        JButton updateButton = new JButton("Update Purchase");
        updateButton.addActionListener(e -> updatePurchase());
        JButton viewButton = new JButton("View Purchase");
        viewButton.addActionListener(e -> viewPurchase());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewDetails());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(viewDetailsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"Purchase No", "Purchase Date", "Customer ID"};
        tableModel = new DefaultTableModel(columns, 0);
        purtable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(purtable);
        scrollPane.getViewport().setBackground(Color.WHITE); 
        add(scrollPane, BorderLayout.CENTER);

        loadPurchases();
    }
    
    private LocalDate parseDate(String text) throws DateTimeParseException {
        if (text == null || text.trim().isEmpty() || text.trim().equalsIgnoreCase("N/A")) {
            return null;
        }
        return LocalDate.parse(text.trim(), dateFormatter);
    }

    private void addPurchase() {
        try {
            int pNo = Integer.parseInt(pNoField.getText().trim()); 
            LocalDate purDate = parseDate(purDateField.getText());
            int cId = Integer.parseInt(cIdField.getText().trim());
            
            purcontroller.addPurchase(new Purchase(pNo, purDate, cId));
            
            JOptionPane.showMessageDialog(this, "Purchase added successfully!");
            clearFields();
            loadPurchases();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for IDs.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
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

            LocalDate purDate = purDateField.getText().trim().isEmpty() ? current.getPurchaseDate() : parseDate(purDateField.getText());
            int cId = cIdField.getText().trim().isEmpty() ? current.getCustomerId() : Integer.parseInt(cIdField.getText().trim());
            
            Purchase updatedP = new Purchase(pNo, purDate, cId);
            purcontroller.updatePurchase(updatedP);
            
            JOptionPane.showMessageDialog(this, "Purchase updated successfully!");
            clearFields();
            loadPurchases();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void viewPurchase() {
        try {
            int pNo = Integer.parseInt(pNoField.getText().trim());
            Purchase p = purcontroller.getPurchaseByNo(pNo);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Purchase No not found.");
                return;
            }
    
            purDateField.setText(p.getPurchaseDate() != null ? p.getPurchaseDate().format(dateFormatter) : "N/A");
            cIdField.setText(String.valueOf(p.getCustomerId()));
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    
    private void viewDetails() {
        int selectedRow = purtable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase from the table to view its details.", "No Purchase Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int purchaseNo = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            List<PurchaseDetailsDisplay> details = purcontroller.getDetailsForPurchase(purchaseNo);
            
            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No details found for Purchase No: " + purchaseNo, "No Details", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog detailsDialog = new JDialog(mainView, "Details for Purchase No: " + purchaseNo, true);
            detailsDialog.setSize(600, 300);
            detailsDialog.setLocationRelativeTo(this);
            
            String[] columns = {"Batch ID", "Medicine Name", "Qty", "Discount", "Total"};
            DefaultTableModel detailsModel = new DefaultTableModel(columns, 0);
            JTable detailsTable = new JTable(detailsModel);
            
            for (PurchaseDetailsDisplay item : details) {
                detailsModel.addRow(new Object[]{
                    item.getMedicineId(),
                    item.getMedicineName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getDiscount()),
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

    private void loadPurchases() {
        tableModel.setRowCount(0);
        try {
            List<Purchase> purs = purcontroller.getAllPurchases();
            for (Purchase p : purs) {
                tableModel.addRow(new Object[]{
                        p.getPurchaseNo(), 
                        p.getPurchaseDate() != null ? p.getPurchaseDate().format(dateFormatter) : "N/A", 
                        p.getCustomerId()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading purchases: " + e.getMessage());
        }
    }

    private void clearFields() {
        pNoField.setText("");
        purDateField.setText("");
        cIdField.setText("");
    }
}