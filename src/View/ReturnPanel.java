package View;

import Model.Return;
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
    private MainView mainView;

    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReturnPanel (MainView mainView) {
        this.mainView = mainView;
        retcontroller = new ReturnController();
        setLayout(new BorderLayout());

        // ===== Top Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Return:"));

        rNoField = new JTextField();
        sIdField = new JTextField();
        reasonField = new JTextField();
        reqDateField = new JTextField();
        shipDateField = new JTextField();
        statusField = new JTextField();

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
        formPanel.add(new JLabel("Return Status"));
        formPanel.add(statusField);

        add(formPanel, BorderLayout.NORTH);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Return");
        addButton.addActionListener(e -> addReturn());
        JButton updateButton = new JButton("Update Return");
        updateButton.addActionListener(e -> updateReturn());
        JButton viewButton = new JButton("View Return");
        viewButton.addActionListener(e -> viewReturn());
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> mainView.goHome());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"Return No", "Supplier ID", "Reason", "Request Date", "Shipped Date", "Return Status"};
        tableModel = new DefaultTableModel(columns, 0);
        retTable = new JTable(tableModel);
        add(new JScrollPane(retTable), BorderLayout.CENTER);

        loadReturns();
    }

    private void addReturn() {
        try {
            int rNo = Integer.parseInt(rNoField.getText().trim());
            int sId = Integer.parseInt(sIdField.getText().trim());
            String reason = reasonField.getText().trim();
            LocalDate reqDate = LocalDate.parse(reqDateField.getText().trim(), dateFormatter);
            LocalDate shipDate = LocalDate.parse(shipDateField.getText().trim(), dateFormatter);
            String status = statusField.getText().trim();

            Return r = new Return(rNo, sId, reason, reqDate, shipDate, status);
            retcontroller.addReturn(r);
            JOptionPane.showMessageDialog(this, "Return added successfully!");
            clearFields();
            loadReturns();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + nfe.getMessage());
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Invalid date format: " + dtpe.getMessage());
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
        }
    }

    private void updateReturn() {
        try {
            int rNo = Integer.parseInt(rNoField.getText().trim());
            int sId = Integer.parseInt(sIdField.getText().trim());
            String reason = reasonField.getText().trim();
            LocalDate reqDate = LocalDate.parse(reqDateField.getText().trim(), dateFormatter);
            LocalDate shipDate = LocalDate.parse(shipDateField.getText().trim(), dateFormatter);
            String status = statusField.getText().trim();

            Return r = new Return(rNo, sId, reason, reqDate, shipDate, status);
            retcontroller.updateReturn(r);
            JOptionPane.showMessageDialog(this, "Return updated successfully!");
            clearFields();
            loadReturns();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + nfe.getMessage());
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Invalid date format: " + dtpe.getMessage());
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
        }
    }

    private void viewReturn() {
        try {
            int rNo = Integer.parseInt(rNoField.getText().trim());
            Return r = retcontroller.getReturnByNo(rNo);
            if (r != null) {
                sIdField.setText(String.valueOf(r.getSupplierId()));
                reasonField.setText(r.getReason());
                reqDateField.setText(r.getRequestDate() != null ? r.getRequestDate().format(dateFormatter) : "N/A");
                shipDateField.setText(r.getShippedDate() != null ? r.getShippedDate().format(dateFormatter) : "N/A");
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
