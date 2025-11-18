package View;

import Controller.ProcurementReportController;
import Model.ProcurementReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProcurementReportPanel extends JPanel {
    private ProcurementReportController controller;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> monthComboBox; // Optimize to Dropdown
    private JTextField yearField;

    public ProcurementReportPanel(NewMainView mainView) 
    {
        controller = new ProcurementReportController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Generate Procurement Report"));

        // Create ComboBox for Month Selection
        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        
        yearField = new JTextField(5); // Set a preferred size

        formPanel.add(new JLabel("Month:"));
        formPanel.add(monthComboBox);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);

        JButton generateButton = new JButton("Generate Report");
        formPanel.add(generateButton);
        
        add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Supplier ID","Supplier Name","Restocks","Total Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Action Listener
        generateButton.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        try {
            int month = (int) monthComboBox.getSelectedItem(); 
            int year = Integer.parseInt(yearField.getText().trim());

            if (year < 2000 || year > 2100) {
                throw new NumberFormatException("Please enter a valid year.");
            }

            List<ProcurementReport> reportData = controller.getProcurementReport(month, year);
            tableModel.setRowCount(0);
            
            tableModel.setRowCount(0);
            for (ProcurementReport item : reportData) {
                tableModel.addRow(new Object[]{
                    item.getSID(),
                    item.getSName(),
                    item.getRestocks(),
                    String.format("%.2f", item.getTotal()) 
                });
            }
            
            if (reportData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No procurement data found for " + month + "/" + year, "Report", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric year (e.g., 2025).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}