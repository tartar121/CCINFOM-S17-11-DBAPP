import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DeliveryPanel extends JPanel {
    private DeliveryController delcontroller;
    private JTable suptable, detailTable;
    private DefaultTableModel tableModel, detailTableModel;
    private JTextField deliveryNoFieldd, supplierIdField, requestDateField, shippedDateField, statusField;
    private JTextField deliveryNoField, medicineIdField, quantityField, totalField;

    public DeliveryPanel(MainView mainView) {
        delcontroller = new DeliveryController();
        setLayout(new BorderLayout());

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel();
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> mainView.goHome());
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.NORTH);

        // --- DELIVERY FORM ---
        JPanel deliveryForm = new JPanel(new GridLayout(7, 2, 5, 5));
        deliveryForm.setBorder(BorderFactory.createTitledBorder("Delivery Info"));
        deliveryNoFieldd = new JTextField();
        supplierIdField = new JTextField();
        requestDateField = new JTextField();
        shippedDateField = new JTextField();
        statusField = new JTextField();

        deliveryForm.add(new JLabel("Delivery no (for updates):"));
        deliveryForm.add(deliveryNoFieldd);
        deliveryForm.add(new JLabel("Supplier ID:"));
        deliveryForm.add(supplierIdField);
        deliveryForm.add(new JLabel("Request Date (YYYY-MM-DD):"));
        deliveryForm.add(requestDateField);
        deliveryForm.add(new JLabel("Shipped Date (YYYY-MM-DD):"));
        deliveryForm.add(shippedDateField);
        deliveryForm.add(new JLabel("Status (Delivered/Cancelled):"));
        deliveryForm.add(statusField);

        JButton addDeliveryBtn = new JButton("Add Delivery");
        addDeliveryBtn.addActionListener(e -> addDelivery());
        deliveryForm.add(addDeliveryBtn);
        JButton updateDeliveryBtn = new JButton ("Update Delivery");
        updateDeliveryBtn.addActionListener(e -> updateDelivery());
        deliveryForm.add(updateDeliveryBtn);
        JButton viewDeliveryBtn = new JButton ("View Delivery");
        viewDeliveryBtn.addActionListener(e -> viewDelivery());
        deliveryForm.add(viewDeliveryBtn);

        // --- DELIVERY DETAILS FORM ---
        JPanel detailForm = new JPanel(new GridLayout(5, 2, 5, 5));
        detailForm.setBorder(BorderFactory.createTitledBorder("Delivery Details"));
        deliveryNoField = new JTextField();
        medicineIdField = new JTextField();
        quantityField = new JTextField();
        totalField = new JTextField();

        detailForm.add(new JLabel("Delivery No:"));
        detailForm.add(deliveryNoField);
        detailForm.add(new JLabel("Medicine ID:"));
        detailForm.add(medicineIdField);
        detailForm.add(new JLabel("Quantity:"));
        detailForm.add(quantityField);
        detailForm.add(new JLabel("Total:"));
        detailForm.add(totalField);

        JButton addDetailBtn = new JButton("Add Delivery Detail");
        addDetailBtn.addActionListener(e -> addDelDetail());
        detailForm.add(addDetailBtn);
        JButton updateDetailBtn = new JButton("Update Delivery Details");
        updateDetailBtn.addActionListener(e -> updateDeliveryd());
        detailForm.add(updateDetailBtn);

        // --- COMBINE FORMS ---
        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        formsPanel.add(deliveryForm);
        formsPanel.add(Box.createVerticalStrut(10));
        formsPanel.add(detailForm);

        add(new JScrollPane(formsPanel), BorderLayout.WEST);

        // --- DELIVERY TABLE ---
        tableModel = new DefaultTableModel(new String[]{"No", "Supplier ID", "Request", "Shipped", "Status"}, 0);
        suptable = new JTable(tableModel);
        suptable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suptable.getSelectionModel().addListSelectionListener(e -> loadDelivers());
        add(new JScrollPane(suptable), BorderLayout.CENTER);

        // --- DELIVERY DETAILS TABLE ---
        detailTableModel = new DefaultTableModel(new String[]{"Delivery No", "Medicine ID", "Quantity", "Total"}, 0);
        detailTable = new JTable(detailTableModel);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        detailTable.getSelectionModel().addListSelectionListener(e -> loadDeliveryDetails());
        add(new JScrollPane(detailTable), BorderLayout.SOUTH);

        loadDelivers();
        loadDeliveryDetails();


    }
    private void addDelivery() {
        try {
            int supplierId = Integer.parseInt(supplierIdField.getText().trim());
            LocalDate reqDate = parseDate(requestDateField.getText().trim(), "Request Date");
            if (reqDate == null) return;

            LocalDate shDate = parseDate(shippedDateField.getText().trim(), "Shipped Date");

            String status = statusField.getText().trim();
            if (!status.equals("Delivered") && !status.equals("Cancelled")) {
                status="";
            }
            delcontroller.addDelivery(new Delivers(0, supplierId, reqDate, shDate, status));

            JOptionPane.showMessageDialog(this, "Delivery added successfully!");
            clearFields();
            loadDelivers();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for IDs.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void updateDelivery()
    {
        try {
            int id = Integer.parseInt(deliveryNoFieldd.getText().trim());
            Delivers current = delcontroller.getDeliversByID(id);
            if (current == null) {
                JOptionPane.showMessageDialog(this, "Delivery ID not found.");
                return;
            }
    
            int nod = deliveryNoFieldd.getText().trim().isEmpty() ? current.getdno() : Integer.parseInt(deliveryNoFieldd.getText().trim());
            int supid= supplierIdField.getText().trim().isEmpty() ? current.getsid() : Integer.parseInt(supplierIdField.getText().trim());
            LocalDate sDate = shippedDateField.getText().trim().isEmpty() ? current.getsdate()
                    : LocalDate.parse(shippedDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate rDate = requestDateField.getText().trim().isEmpty() ? current.getrdate()
                    : LocalDate.parse(requestDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String status = statusField.getText().trim().isEmpty() ? current.getStatus() : statusField.getText().trim(); 
    
            Delivers updated = new Delivers(nod, supid, rDate, sDate, status);
            delcontroller.updateDelivery(updated);
    
            JOptionPane.showMessageDialog(this, "Delivery updated successfully!");
            clearFields();
            loadDelivers();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void viewDelivery() {
        try {
            int id = Integer.parseInt(deliveryNoFieldd.getText().trim());
    
            // Fetch medicine from database
            Delivers d = delcontroller.getDeliversByID(id);
            if (d == null) {
                JOptionPane.showMessageDialog(this, "Delivery ID not found.");
                return;
            }
    
            // Display in fields
            supplierIdField.setText(String.valueOf(d.getsid()));
            requestDateField.setText(String.valueOf(d.getrdate().toString()));
            shippedDateField.setText(String.valueOf(d.getsdate().toString()));
            statusField.setText(String.valueOf(d.getStatus()));
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void updateDeliveryd()
    {
        try
        {
            int deliveryNo = Integer.parseInt(deliveryNoField.getText().trim());
            int medicineId = Integer.parseInt(medicineIdField.getText().trim());
            List<DeliveryDetails> list = delcontroller.getDeliveryDetailsByDeliveryNo(deliveryNo);
            DeliveryDetails current = null;
            for (DeliveryDetails d : list) {
                if (d.getMedid() == medicineId) {
                    current = d;
                    break;
                }
            }
            if (current == null) {
                JOptionPane.showMessageDialog(this, 
                "Delivery Detail not found for Delivery No " + deliveryNo + " and Medicine ID " + medicineId);
                return;
            }
            int quantity = quantityField.getText().trim().isEmpty() 
                ? current.getQuan() 
                : Integer.parseInt(quantityField.getText().trim());

            double total = totalField.getText().trim().isEmpty()
                ? current.getTotal()
                : Double.parseDouble(totalField.getText().trim());
            DeliveryDetails updated = new DeliveryDetails(deliveryNo, medicineId, quantity, total);
            delcontroller.updateDeliveryDetail(updated);

            JOptionPane.showMessageDialog(this, "Delivery Detail updated successfully!");
            clearFields();
            loadDeliveryDetails();
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private LocalDate parseDate(String dateStr, String fieldName) 
    {
        if (dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid " + fieldName + " format. Use YYYY-MM-DD.");
            return null;
        }
    }

    private void addDelDetail() {
        try {
            int deliveryNo = Integer.parseInt(deliveryNoField.getText().trim());
            int medicineId = Integer.parseInt(medicineIdField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double total = Double.parseDouble(totalField.getText().trim());

            delcontroller.addDeliveryDetail(new DeliveryDetails(deliveryNo, medicineId, quantity, total));
            JOptionPane.showMessageDialog(this, "Delivery Detail added successfully!");
            clearFields();
            loadDeliveryDetails(); // refresh details table if delivery selected
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void loadDelivers() {
        tableModel.setRowCount(0); // clear existing rows
        try {
            List<Delivers> deliveries = delcontroller.getAllDeliveries();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Delivers d : deliveries) {
                String requestDate = d.getrdate() != null ? d.getrdate().format(formatter) : "";
                String shippedDate = d.getsdate() != null ? d.getsdate().format(formatter) : "";
                tableModel.addRow(new Object[]{d.getdno(), d.getsid(), requestDate, shippedDate, d.getStatus()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading deliveries: " + e.getMessage());
        }
    }

    private void loadDeliveryDetails() {
        detailTableModel.setRowCount(0); // clear details
        
        try {
            List<DeliveryDetails> details = delcontroller.getAllDeliveryDetails();
            for (DeliveryDetails d : details) {
                detailTableModel.addRow(new Object[]{d.getDno(), d.getMedid(), d.getQuan(), d.getTotal()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading delivery details: " + e.getMessage());
        }
    }
    private void clearFields() {
        supplierIdField.setText("");
        requestDateField.setText("");
        shippedDateField.setText("");
        statusField.setText("");
        deliveryNoField.setText("");
        medicineIdField.setText("");
        quantityField.setText("");
        totalField.setText("");
    }
}
