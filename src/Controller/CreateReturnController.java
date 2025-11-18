package Controller;

import DB.Database;
import Model.ReturnableItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateReturnController {

    /**
     * Finds all expired or discontinued items from a specific supplier
     * that still have stock. This implements proposal steps 4.3(a), (b), and (c).
     * User logic ^^
     */
    public List<ReturnableItem> findReturnableItems(int supplierId) throws SQLException {
        List<ReturnableItem> items = new ArrayList<>();
        Connection con = Database.connectdb();
        
        // This query joins 4 tables to get all the info we need,
        // fulfilling the proposal's requirement to check multiple records.
        String sql = "SELECT s.supplier_status, m.medicine_id, m.medicine_name, m.expiration_date, " +
                     "m.quantity_in_stock, d.delivery_no, d.shipped_date, m.price_bought " +
                     "FROM medicine m " +
                     "JOIN delivery_details dd ON m.medicine_id = dd.medicine_id " +
                     "JOIN delivers d ON dd.delivery_no = d.delivery_no " +
                     "JOIN supplier s ON d.supplier_id = s.supplier_id " +
                     "WHERE d.supplier_id = ? " +
                     "AND (m.discontinued = true OR m.expiration_date <= CURDATE()) " + // Rule: expired/discontinued
                     "AND m.quantity_in_stock > 0 " +
                     "AND DATEDIFF(CURDATE(), d.shipped_date) <= 14 " +
                     "AND d.delivery_status = 'Delivered' "; // Rule: within 14 days of shipped_date
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, supplierId);
        
        ResultSet rs = ps.executeQuery();

        if (!rs.isBeforeFirst()) { // No items found
            con.close();
            throw new SQLException("No returnable (discontinued/expired/undelivered/old) items found for Supplier ID: " + supplierId);
        }

        // Check supplier status (Business Rule from proposal)
        rs.next();
        if (rs.getString("supplier_status").equals("inactive")) {
            con.close();
            throw new SQLException("Cannot process return. Supplier is inactive.");
        }
        
        // Loop through all items and add them to the list
        do {
            LocalDate shipDate = (rs.getDate("shipped_date") != null) ? rs.getDate("shipped_date").toLocalDate() : null;
            
            items.add(new ReturnableItem(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getInt("quantity_in_stock"),
                rs.getDate("expiration_date").toLocalDate(),
                rs.getInt("delivery_no"),
                shipDate,
                rs.getDouble("price_bought")
            ));
        } while (rs.next());
        
        con.close();
        return items;
    }

    /**
     * Processes the return as a REQUEST.
     * This implements the correct "workflow" logic.
     * It does NOT update the medicine stock.
     */
    public void processReturn(int supplierId, List<ReturnableItem> itemsToReturn) throws SQLException {
        Connection con = Database.connectdb();
        try {
            // Turn off auto-commit and start Transaction
            con.setAutoCommit(false); 

            // Create the main `return` record with status 'Requested'
            String sqlReturn = "INSERT INTO `return` (supplier_id, reason, request_date, shipped_date, return_status) " +
                               "VALUES (?, ?, CURDATE(), NULL, NULL)"; // <-- 1. FIX: Status is 'Requested'
            PreparedStatement psReturn = con.prepareStatement(sqlReturn, Statement.RETURN_GENERATED_KEYS);
            psReturn.setInt(1, supplierId);
            psReturn.setString(2, "Expired/Discontinued");
            psReturn.executeUpdate();

            // Get the new auto-generated return_no
            ResultSet rsKeys = psReturn.getGeneratedKeys();
            int returnNo;
            if (rsKeys.next()) {
                returnNo = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create return record, no ID obtained.");
            }

            // Prepare batch statement for `return_details` ONLY
            String sqlDetails = "INSERT INTO return_details (return_no, medicine_id, delivery_no, " +
                                "price_returned, quantity_returned) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetails = con.prepareStatement(sqlDetails);

            String sqlUpdateStock = "UPDATE medicine SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ?";
            PreparedStatement psUpdateStock = con.prepareStatement(sqlUpdateStock);

            for (ReturnableItem item : itemsToReturn) {
                // Add to return_details batch
                psDetails.setInt(1, returnNo);
                psDetails.setInt(2, item.getMedicineId());
                psDetails.setInt(3, item.getDeliveryNo());
                psDetails.setDouble(4, item.getTotal()); 
                psDetails.setInt(5, item.getQuantity());
                psDetails.addBatch();

                psUpdateStock.setInt(1, item.getQuantity());       // Remove this many to stock
                psUpdateStock.setInt(2, item.getMedicineId());    // For this batch
                psUpdateStock.addBatch();
            }
            
            // Execute ONLY the details batch
            psDetails.executeBatch();
            psUpdateStock.executeBatch();
            
            // If all queries worked, commit the transaction
            con.commit();
            
        } catch (SQLException e) {
            // If any query failed, roll back all changes
            con.rollback(); 
            throw e; 
        } finally {
            // Always turn auto-commit back on and close
            con.setAutoCommit(true);
            con.close();
        }
    }
}