package View;

import Model.Return;
import Model.ReturnDetailsDisplay;
import Controller.ReturnController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReturnPanel extends JPanel{
    private ReturnController retcontroller;
    private JTable retTable;
    private DefaultTableModel tableModel;
    private JTextField rNoField, sIdField, reasonField, reqDateField, shipDateField, statusField;
    private NewMainView mainView;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReturnPanel (NewMainView mainView) {
        this.mainView = mainView;
        retcontroller = new ReturnController();
        
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Top Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Return Records"));

        // JTextFields
        rNoField = new JTextField();
        sIdField = new JTextField();
        reasonField = new JTextField();
        reqDateField = new JTextField();
        shipDateField = new JTextField();
        statusField = new JTextField();
        // JLabels and formPanel.add calls
        formPanel.add(new JLabel("Return No (for View/Update)"));
        formPanel.add(rNoField);
        formPanel.add(new JLabel("Supplier ID"));
        formPanel.add(sIdField);
        formPanel.add(new JLabel("Reason"));
        formPanel.add(reasonField);
        formPanel.add(new JLabel("Request Date (YYYY-MM-DD)"));
        formPanel.add(reqDateField);
        formPanel.add(new JLabel("Shipped Date (YYYY-MM-DD)"));
        formPanel.add(shipDateField);
        formPanel.add(new JLabel("Status (Returned/Cancelled)"));
        formPanel.add(statusField);

        add(formPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = new JButton("Add Return");
        addButton.addActionListener(e -> addReturn());
        JButton updateButton = new JButton("Update Return");
        updateButton.addActionListener(e -> updateReturn());
        JButton viewButton = new JButton("View Return");
        viewButton.addActionListener(e -> viewReturn());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewDetails());

        // Refresh Button 
        JButton refreshButton = new JButton("Refresh Table");
        refreshButton.addActionListener(e -> loadReturns());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton); // Add refresh button to reload table data
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"Return No", "Supplier ID", "Reason", "Request Date", "Shipped Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        retTable = new JTable(tableModel);
        add(new JScrollPane(retTable), BorderLayout.CENTER);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(retTable);
        scrollPane.getViewport().setBackground(Color.WHITE); // <Set Background
        add(scrollPane, BorderLayout.CENTER);

        loadReturns();
    }
    
    private LocalDate parseDate(String text) throws DateTimeParseException {
        if (text == null || text.trim().isEmpty() || text.trim().equalsIgnoreCase("N/A")) {
            return null;
        }
        return LocalDate.parse(text.trim(), dateFormatter);
    }

    // addReturn, updateReturn, viewReturn methods
    private void addReturn() {
        try {
            int rNo = Integer.parseInt(rNoField.getText().trim()); 
            int sId = Integer.parseInt(sIdField.getText().trim());
            String reason = reasonField.getText().trim();
            LocalDate reqDate = parseDate(reqDateField.getText());
            LocalDate shipDate = parseDate(shipDateField.getText());
            String status = statusField.getText().trim();

            Return r = new Return(rNo, sId, reason, reqDate, shipDate, status);
            retcontroller.addReturn(r);
            
            JOptionPane.showMessageDialog(this, "Return added successfully!");
            clearFields();
            loadReturns();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for IDs.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void updateReturn() {
        try {
            int rNo = Integer.parseInt(rNoField.getText().trim());
            
            // Get the 'current' record to find the OLD status
            Return current = retcontroller.getReturnByNo(rNo);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Return No not found.");
                return;
            }
            String oldStatus = current.getReturnStatus(); // Get the old status

            // Get the new values from the form
            int sId = sIdField.getText().trim().isEmpty() ? current.getSupplierId() : Integer.parseInt(sIdField.getText().trim());
            String reason = reasonField.getText().trim().isEmpty() ? current.getReason() : reasonField.getText().trim();
            LocalDate reqDate = parseDate(reqDateField.getText());
            LocalDate shipDate = parseDate(shipDateField.getText());
            String status = statusField.getText().trim().isEmpty() ? current.getReturnStatus() : statusField.getText().trim();
            
            Return updated = new Return(rNo, sId, reason, reqDate, shipDate, status);
            
            // Call the new update method
            retcontroller.updateReturn(updated, oldStatus); 
            
            JOptionPane.showMessageDialog(this, "Return updated successfully!");
            clearFields();
            loadReturns();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void viewReturn() {
        try {
            int rNo = Integer.parseInt(rNoField.getText().trim());
            Return r = retcontroller.getReturnByNo(rNo);
            
            if (r != null) {
                sIdField.setText(String.valueOf(r.getSupplierId()));
                reasonField.setText(r.getReason());
                reqDateField.setText(r.getRequestDate() != null ? r.getRequestDate().format(dateFormatter) : "");
                shipDateField.setText(r.getShippedDate() != null ? r.getShippedDate().format(dateFormatter) : "");
                statusField.setText(r.getReturnStatus());
            } else {
                JOptionPane.showMessageDialog(this, "Return not found.");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + nfe.getMessage());
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
        }
    }
    
    private void viewDetails() {
        int selectedRow = retTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a return from the table to view its details.", "No Return Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int returnNo = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            List<ReturnDetailsDisplay> details = retcontroller.getDetailsForReturn(returnNo);
            
            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No details found for Return No: " + returnNo, "No Details", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog detailsDialog = new JDialog(mainView, "Details for Return No: " + returnNo, true);
            detailsDialog.setSize(500, 300);
            detailsDialog.setLocationRelativeTo(this);
            
            String[] columns = {"Batch ID", "Medicine Name", "Qty Returned", "Price Returned"};
            DefaultTableModel detailsModel = new DefaultTableModel(columns, 0);
            JTable detailsTable = new JTable(detailsModel);
            
            for (ReturnDetailsDisplay item : details) {
                detailsModel.addRow(new Object[]{
                    item.getMedicineId(),
                    item.getMedicineName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getPrice())
                });
            }
            
            JScrollPane scrollPane = new JScrollPane(detailsTable); // Put table in a scroll pane
            scrollPane.getViewport().setBackground(Color.WHITE); // Set background
            detailsDialog.add(scrollPane);
            
            detailsDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReturns() {
        try {
            List<Return> returns = retcontroller.getAllReturns();
            tableModel.setRowCount(0); // Clear existing data
            for (Return r : returns) {
                Object[] row = {
                    r.getReturnNo(),
                    r.getSupplierId(),
                    r.getReason(),
                    r.getRequestDate() != null ? r.getRequestDate().format(dateFormatter) : "N/A",
                    r.getShippedDate() != null ? r.getShippedDate().format(dateFormatter) : "N/A",
                    r.getReturnStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
        }
    }

    private void clearFields() {
        rNoField.setText("");
        sIdField.setText("");
        reasonField.setText("");
        reqDateField.setText("");
        shipDateField.setText("");
        statusField.setText("");
    }
}