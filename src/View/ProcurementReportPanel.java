package View;

import Controller.ProcurementReportController;
import Model.ProcurementReport;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProcurementReportPanel extends JPanel{
    private ProcurementReportController proController;
    private DefaultTableModel tableModel;
    private JTable reportTable;
    private JTextField month, year;
    public ProcurementReportPanel (MainView mainView)
    {
        proController= new ProcurementReportController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel= new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton homeButton= new JButton("Back to Home");
        homeButton.addActionListener(e -> mainView.goHome());
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.NORTH);

        month= new JTextField(10);
        year= new JTextField(10);

        JPanel proReport= new JPanel(new GridLayout(3, 2, 5, 5));
        proReport.add(new JLabel("Input month:"));
        proReport.add(month);
        proReport.add(new JLabel("Input year: "));
        proReport.add(year);
        JButton generateReport= new JButton("Generate Report");
        generateReport.addActionListener(e -> getReport());
        proReport.add(new JLabel());
        proReport.add(generateReport);
        add(proReport, BorderLayout.WEST);
        

        String[] columns = {"Supplier ID", "Supplier Name", "Restocks", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0);

        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);
    }
    private void getReport()
    {
        tableModel.setRowCount(0);
        if (month.getText().isEmpty() || year.getText().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Please enter both month and year.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            int m= Integer.parseInt(month.getText().trim());
            int y= Integer.parseInt(year.getText().trim());

            if (m < 1 || m > 12) {
                JOptionPane.showMessageDialog(this, "Month must be between 1 and 12.",
                        "Invalid Month", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<ProcurementReport> report= proController.getProcurementReport(m, y);
            if (report.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No restocking records found for this period.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (ProcurementReport r: report)
            {
                tableModel.addRow(new Object[]{
                    r.getSID(),
                    r.getSName(),
                    r.getRestocks(),
                    r.getTotal()
                });
            }
        }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Month and Year must be numeric.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading deliveries: " + e.getMessage());
        }
    }
}
