package Controller;

import DB.Database;
import Model.NewDeliveryItem;     
import Model.Supplier;            
import Model.Medicine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateDeliveryController {

    /**
     * Checks if a supplier is 'active' before processing a delivery.
     * This implements proposal step 4.2(b).
     */
    public Supplier findSupplier(int supplierId) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM supplier WHERE supplier_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, supplierId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Supplier s = new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("supplier_address"),
                rs.getString("supplier_contact_info"),
                rs.getString("supplier_status")
            );
            con.close();
            
            if (s.getStatus().equals("inactive")) {
                throw new SQLException("Supplier (ID: " + supplierId + ") is 'inactive'. Cannot receive delivery.");
            }
            return s;
        } else {
            con.close();
            throw new SQLException("Supplier ID not found.");
        }
    }
    public Medicine findMedicineBatch(int medicineId) throws SQLException {
        Connection con = Database.connectdb();
        // Checks all business rules for selling
        String sql = "SELECT * FROM medicine WHERE medicine_id = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, medicineId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Medicine m = new Medicine(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getDouble("price_bought"),
                rs.getDouble("price_for_sale"),
                rs.getInt("quantity_in_stock"),
                rs.getDate("expiration_date").toLocalDate(),
                rs.getBoolean("discontinued")
            );
            con.close();
            /* don't need this since delivery doesn't check for quantity, exp date, and discontinued
            // Check rules that the trigger *doesn't* check
            if (m.getQuantity() <= 0) {
                throw new SQLException("Medicine '" + m.getName() + "' (Batch ID: " + m.getId() + ") is out of stock.");
            }
            // Note: The trigger `prevent_expired_or_discontinued_sale`
            // will automatically block expired/discontinued items, but we
            // check here *before* the transaction to give a better error.
            if (m.isDiscontinued()) {
                throw new SQLException("Medicine '" + m.getName() + "' is discontinued.");
            }
            if (m.getExpirationDate().isBefore(LocalDate.now())) {
                 throw new SQLException("Medicine '" + m.getName() + "' (Batch ID: " + m.getId() + ") is expired.");
            }*/
            return m;
        } else {
            con.close();
            throw new SQLException("Medicine Batch ID not found.");
        }
    }
    /**
     * Processes the delivery as a REQUEST.
     * This implements the correct "workflow" logic.
     * It does NOT add stock.
     */
    public int processDelivery(int supplierId, List<NewDeliveryItem> itemsToDeliver) throws SQLException {
        Connection con = Database.connectdb();
        try {
            con.setAutoCommit(false); // START TRANSACTION

            // Create the main `delivers` record with status 'Requested'
            String sqlDeliver = "INSERT INTO delivers (supplier_id, request_date, shipped_date, delivery_status) " +
                                "VALUES (?, CURDATE(), NULL, NULL)"; // <-- 1. FIX: Status is 'Requested'
            PreparedStatement psDeliver = con.prepareStatement(sqlDeliver, Statement.RETURN_GENERATED_KEYS);
            psDeliver.setInt(1, supplierId);
            psDeliver.executeUpdate();

            // Get the new auto-generated delivery_no
            ResultSet rsKeys = psDeliver.getGeneratedKeys();
            int deliveryNo;
            if (rsKeys.next()) {
                deliveryNo = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create delivery record, no ID obtained.");
            }

            // Prepare batch statements
            
            // This fulfills "Adding a new medicine record (batch)"
            // We set stock to 0 because it hasn't arrived yet.

            // This fulfills "Recording the link... in delivery_details"
            String sqlDetails = "INSERT INTO delivery_details (delivery_no, medicine_id, quantity, total) " +
                                "VALUES (?, ?, ?, ?)";
            PreparedStatement psDetails = con.prepareStatement(sqlDetails);

            String sqlUpdateStock = "UPDATE medicine SET quantity_in_stock = quantity_in_stock + ? WHERE medicine_id = ?";
            PreparedStatement psUpdateStock = con.prepareStatement(sqlUpdateStock);


            for (NewDeliveryItem item : itemsToDeliver) {
                // Add new batch to `medicine` table
                // Link this batch to the delivery in `delivery_details`
                psDetails.setInt(1, deliveryNo);
                psDetails.setInt(2, item.getMedicineId()); // use existing ID
                psDetails.setInt(3, item.getQuantity());
                psDetails.setDouble(4, item.getPriceBought() * item.getQuantity());
                psDetails.addBatch();

                psUpdateStock.setInt(1, item.getQuantity());       // Add this many to stock
                psUpdateStock.setInt(2, item.getMedicineId());    // For this batch
                psUpdateStock.addBatch();
            }
            
            // Execute all batches
            psDetails.executeBatch();
            psUpdateStock.executeBatch();

            // If all queries worked, commit the transaction
            con.commit();
            return deliveryNo;
            
        } catch (SQLException e) {
            con.rollback(); // ROLLBACK TRANSACTION
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Transaction Failed: A 'Batch ID' you entered already exists. Please use a unique ID.");
            }
            throw e; // Re-throw other exceptions
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
}