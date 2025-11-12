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
    private JTextField idField, nameField, priceBoughtField, priceSaleField, qtyField, expDateField;
    private MainView mainView;

    public MedicinePanel(MainView mainView) {
        this.mainView = mainView;
        medcontroller = new MedicineController();
        setLayout(new BorderLayout());

        // ===== Top Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Medicine"));

        idField = new JTextField();
        nameField = new JTextField();
        priceBoughtField = new JTextField();
        priceSaleField = new JTextField();
        qtyField = new JTextField();
        expDateField = new JTextField();

        formPanel.add(new JLabel("ID"));
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

        add(formPanel, BorderLayout.NORTH);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Medicine");
        addButton.addActionListener(e -> addMedicine());
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> mainView.goHome());

        buttonPanel.add(addButton);
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Table =====
        String[] columns = {"ID", "Name", "Bought", "For Sale", "Qty", "Expiration", "Discontinued"};
        tableModel = new DefaultTableModel(columns, 0);
        medtable = new JTable(tableModel);
        add(new JScrollPane(medtable), BorderLayout.CENTER);

        loadMedicines();
    }

    private void addMedicine() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
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

            medcontroller.addMedicine(new Medicine(id, name, priceBought, priceForSale, qty, expDate, false));
            JOptionPane.showMessageDialog(this, "Medicine added successfully!");
            clearFields();
            loadMedicines();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID, prices, and quantity.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void loadMedicines() {
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
    }
}
