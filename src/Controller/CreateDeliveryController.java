package Controller;

import DB.Database;
import Model.NewDeliveryItem;     // The "Shopping List" 📝
import Model.Supplier;            // The Core Record
import Model.Delivers;            // The "Official Record" 🗄️
import Model.DeliveryDetails;     // The "Official Record" 🗄️

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

    /**
     * Processes the delivery as a REQUEST.
     * This implements the correct "workflow" logic.
     * It does NOT add stock.
     */
    public void processDelivery(int supplierId, List<NewDeliveryItem> itemsToDeliver) throws SQLException {
        Connection con = Database.connectdb();
        try {
            con.setAutoCommit(false); // START TRANSACTION

            // 1. Create the main `delivers` record with status 'Requested'
            String sqlDeliver = "INSERT INTO delivers (supplier_id, request_date, shipped_date, delivery_status) " +
                                "VALUES (?, CURDATE(), NULL, 'Requested')"; // <-- 1. FIX: Status is 'Requested'
            PreparedStatement psDeliver = con.prepareStatement(sqlDeliver, Statement.RETURN_GENERATED_KEYS);
            psDeliver.setInt(1, supplierId);
            psDeliver.executeUpdate();

            // 2. Get the new auto-generated delivery_no
            ResultSet rsKeys = psDeliver.getGeneratedKeys();
            int deliveryNo;
            if (rsKeys.next()) {
                deliveryNo = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create delivery record, no ID obtained.");
            }

            // 3. Prepare batch statements
            
            // This fulfills "Adding a new medicine record (batch)"
            // We set stock to 0 because it hasn't arrived yet.
            String sqlMedicine = "INSERT INTO medicine (medicine_id, medicine_name, price_bought, price_for_sale, " +
                                 "quantity_in_stock, expiration_date, discontinued) VALUES (?, ?, ?, ?, 0, ?, false)"; // <-- 2. FIX: Qty is 0
            PreparedStatement psMedicine = con.prepareStatement(sqlMedicine);

            // This fulfills "Recording the link... in delivery_details"
            String sqlDetails = "INSERT INTO delivery_details (delivery_no, medicine_id, quantity, total) " +
                                "VALUES (?, ?, ?, ?)";
            PreparedStatement psDetails = con.prepareStatement(sqlDetails);

            for (NewDeliveryItem item : itemsToDeliver) {
                // Add new batch to `medicine` table
                psMedicine.setInt(1, item.getMedicineId());
                psMedicine.setString(2, item.getName());
                psMedicine.setDouble(3, item.getPriceBought());
                psMedicine.setDouble(4, item.getPriceForSale());
                psMedicine.setDate(5, java.sql.Date.valueOf(item.getExpDate())); // <-- 3. FIX: Parameter index
                psMedicine.addBatch();
                
                // Link this batch to the delivery in `delivery_details`
                psDetails.setInt(1, deliveryNo);
                psDetails.setInt(2, item.getMedicineId());
                psDetails.setInt(3, item.getQuantity()); // This is the *requested* quantity
                psDetails.setDouble(4, item.getPriceBought() * item.getQuantity());
                psDetails.addBatch();
            }
            
            // 4. Execute all batches
            psMedicine.executeBatch();
            psDetails.executeBatch();

            // 5. If all queries worked, commit the transaction
            con.commit();
            
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