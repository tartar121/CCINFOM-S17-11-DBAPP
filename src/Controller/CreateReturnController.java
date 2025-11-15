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
     *
     */
    public List<ReturnableItem> findReturnableItems(int supplierId) throws SQLException {
        List<ReturnableItem> items = new ArrayList<>();
        Connection con = Database.connectdb();
        
        // This query joins 4 tables to get all the info we need,
        // fulfilling the proposal's requirement to check multiple records.
        // It correctly uses the medicine table as a batch table.
        String sql = "SELECT s.supplier_status, m.medicine_id, m.medicine_name, m.expiration_date, " +
                     "m.quantity_in_stock, d.delivery_no, d.shipped_date, m.price_bought " +
                     "FROM medicine m " +
                     "JOIN delivery_details dd ON m.medicine_id = dd.medicine_id " +
                     "JOIN delivers d ON dd.delivery_no = d.delivery_no " +
                     "JOIN supplier s ON d.supplier_id = s.supplier_id " +
                     "WHERE d.supplier_id = ? " +
                     "AND (m.discontinued = true OR m.expiration_date <= CURDATE()) " + // Rule: expired/discontinued
                     "AND m.quantity_in_stock > 0";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, supplierId);
        
        ResultSet rs = ps.executeQuery();

        if (!rs.isBeforeFirst()) { // No items found
            con.close();
            throw new SQLException("No returnable (expired/discontinued) items found for Supplier ID: " + supplierId);
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
            
            // This logic implements the 7-day rule check by fetching the shipped_date.
            // Your proposal is a bit ambiguous if the 7-day rule is for *all* returns
            // or just some. Here, we fetch the date so you can enforce it if needed.
            // For now, we'll just return all expired/discontinued items.
            
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
     * Processes the entire return as a single, atomic SQL transaction.
     * This implements proposal steps 4.3(d) and (e).
     *
     */
    public void processReturn(int supplierId, List<ReturnableItem> itemsToReturn) throws SQLException {
        Connection con = Database.connectdb();
        try {
            // 1. Turn off auto-commit. This makes it a single transaction.
            con.setAutoCommit(false);

            // 2. Create the main `return` record
            String sqlReturn = "INSERT INTO `return` (supplier_id, reason, request_date, return_status) " +
                               "VALUES (?, ?, CURDATE(), 'Returned')";
            PreparedStatement psReturn = con.prepareStatement(sqlReturn, Statement.RETURN_GENERATED_KEYS);
            psReturn.setInt(1, supplierId);
            psReturn.setString(2, "Expired/Discontinued");
            psReturn.executeUpdate();

            // 3. Get the new auto-generated return_no
            ResultSet rsKeys = psReturn.getGeneratedKeys();
            int returnNo;
            if (rsKeys.next()) {
                returnNo = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create return record, no ID obtained.");
            }

            // 4. Prepare batch statements for `return_details` and `medicine` updates
            String sqlDetails = "INSERT INTO return_details (return_no, medicine_id, delivery_no, " +
                                "price_returned, quantity_returned) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetails = con.prepareStatement(sqlDetails);

            // This fulfills "Updating medicine record"
            String sqlUpdate = "UPDATE medicine SET quantity_in_stock = 0, discontinued = true " +
                               "WHERE medicine_id = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);

            for (ReturnableItem item : itemsToReturn) {
                // Add to return_details batch
                psDetails.setInt(1, returnNo);
                psDetails.setInt(2, item.getMedicineId());
                psDetails.setInt(3, item.getDeliveryNo());
                psDetails.setDouble(4, item.getPriceBought()); // Price returned is the price we bought it for
                psDetails.setInt(5, item.getQuantity());
                psDetails.addBatch();
                
                // Add to medicine update batch (sets stock to 0)
                psUpdate.setInt(1, item.getMedicineId());
                psUpdate.addBatch();
            }
            
            // 5. Execute both batches
            psDetails.executeBatch();
            psUpdate.executeBatch();

            // 6. If all queries worked, commit the transaction
            con.commit();
            
        } catch (SQLException e) {
            // 7. If any query failed, roll back all changes
            con.rollback();
            throw e; // Re-throw the exception to notify the panel
        } finally {
            // 8. Always turn auto-commit back on and close
            con.setAutoCommit(true);
            con.close();
        }
    }
}