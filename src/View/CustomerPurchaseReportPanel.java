package View;

import Controller.CustomerPurchaseReportController;
import Model.CustomerPurchaseReport;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerPurchaseReportPanel extends JPanel{
    private CustomerPurchaseReportController cpController;
    private DefaultTableModel tableModel;
    private JTable reportTable;
    private JComboBox<Integer> monthComboBox;
    private JTextField yearField;
    public CustomerPurchaseReportPanel (NewMainView mainView)
    {
        cpController= new CustomerPurchaseReportController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Generate Customer Purchase Report"));

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

        String[] columns = {"Customer ID", "Customer Name", "Sold", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0);

        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        generateButton.addActionListener(e -> getReport());
    }
    private void getReport()
    {
        try {
            int month = (int) monthComboBox.getSelectedItem(); 
            int year = Integer.parseInt(yearField.getText().trim());

            if (year < 2000 || year > 2100) {
                throw new NumberFormatException("Please enter a valid year.");
            }

            List<CustomerPurchaseReport> report= cpController.getCusomerPurchaseReport(month, year);
            if (report.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No selling records found for this period.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (CustomerPurchaseReport r: report)
            {
                tableModel.addRow(new Object[]{
                    r.getCustomerId(),
                    r.getCustomerName(),
                    r.getSelling(),
                    r.getTotal()
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric year (e.g., 2025).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
