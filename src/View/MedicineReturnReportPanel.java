package View;

import Controller.MedicineReturnReportController;
import Model.MedicineReturnReport;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicineReturnReportPanel extends JPanel{
    private MedicineReturnReportController medController;
    private DefaultTableModel tableModel;
    private JTable reportTable;
    private JComboBox<Integer> monthComboBox;
    private JTextField yearT;
    public MedicineReturnReportPanel (NewMainView mainView)
    {
        medController= new MedicineReturnReportController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Generate Medicine Return Report"));

        // Create ComboBox for Month Selection
        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        
        yearT = new JTextField(5); // Set a preferred size

        formPanel.add(new JLabel("Month:"));
        formPanel.add(monthComboBox); 
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearT);

        JButton generateButton = new JButton("Generate Report");
        formPanel.add(generateButton);
        
        add(formPanel, BorderLayout.NORTH);        

        String[] columns = {"Medicine ID", "Medicine Name", "No. of Returns", "Quantity Returned", "Price Returned"};
        tableModel = new DefaultTableModel(columns, 0);

        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        generateButton.addActionListener(e -> getReport());
    }
    private void getReport()
    {
        try {
            int month = (int) monthComboBox.getSelectedItem(); 
            int year = Integer.parseInt(yearT.getText().trim());

            if (year < 2000 || year > 2100) {
                throw new NumberFormatException("Please enter a valid year.");
            }

            List<MedicineReturnReport> report= medController.getMedicineReturnReport(month, year);
            tableModel.setRowCount(0);
            if (report.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No returns records found for this period.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (MedicineReturnReport mr: report)
            {
                tableModel.addRow(new Object[]{
                    mr.getMedID(),
                    mr.getName(),
                    mr.getNoReturns(),
                    mr.getTotalQ(),
                    mr.getTotalR()
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric year (e.g., 2025).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
