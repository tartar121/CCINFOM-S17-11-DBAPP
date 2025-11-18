package Controller;

import DB.Database;
import Model.CartItem;
import Model.Customer; 
import Model.Medicine; 

import java.sql.*;
import java.util.List;
import java.time.LocalDate;

public class CreatePurchaseController {

    /**
     * Finds a customer and checks if they are active.
     * This implements proposal step 4.1(a).
     */
    public Customer findCustomer(int customerId) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Customer c = new Customer(
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_contact_info"),
                rs.getInt("senior_pwd_id"),
                rs.getString("customer_status")
            );
            con.close();

            // This is the business rule
            if (c.getStatus().equals("inactive")) {
                throw new SQLException("Customer (ID: " + customerId + ") is 'inactive'. Cannot make a purchase.");
            }
            return c;
        } else {
            con.close();
            throw new SQLException("Customer ID not found.");
        }
    }

    /**
     * Finds a medicine batch and checks if it's sellable.
     * This implements proposal step 4.1(b).
     */
    public Medicine findMedicineBatch(int medicineId) throws SQLException {
        Connection con = Database.connectdb();
        // This query checks all business rules for selling
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

            // Check rules that the trigger doesn't check
            if (m.getQuantity() <= 0) {
                throw new SQLException("Medicine '" + m.getName() + "' (Batch ID: " + m.getId() + ") is out of stock.");
            }

            if (m.isDiscontinued()) {
                throw new SQLException("Medicine '" + m.getName() + "' is discontinued.");
            }
            if (m.getExpirationDate().isBefore(LocalDate.now())) {
                 throw new SQLException("Medicine '" + m.getName() + "' (Batch ID: " + m.getId() + ") is expired.");
            }
            return m;
        } else {
            con.close();
            throw new SQLException("Medicine Batch ID not found.");
        }
    }

    /**
     * Processes the entire purchase as a single, atomic SQL transaction.
     * This implements proposal steps 4.1(c), (d), and (e).
     * Returns the new purchase_no for the receipt.
     */
    public int processPurchase(int customerId, List<CartItem> cart) throws SQLException {
        Connection con = Database.connectdb();
        try {
            con.setAutoCommit(false); // START TRANSACTION
            
            // Create the main `purchase` record (Step 4.1.c)
            String sqlPurchase = "INSERT INTO purchase (customer_id, purchase_date) VALUES (?, CURDATE())";
            PreparedStatement psPurchase = con.prepareStatement(sqlPurchase, Statement.RETURN_GENERATED_KEYS);
            psPurchase.setInt(1, customerId);
            psPurchase.executeUpdate();

            // Get the new auto-generated purchase_no
            ResultSet rsKeys = psPurchase.getGeneratedKeys();
            int purchaseNo;
            if (rsKeys.next()) {
                purchaseNo = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create purchase record, no ID obtained.");
            }

            
            // "Recording... in purchase_details" (Step 4.1.d)
            String sqlDetails = "INSERT INTO purchase_details (purchase_no, medicine_id, quantity_ordered, discount, total) " +
                                "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetails = con.prepareStatement(sqlDetails);

            // "Updating the medicine record" (Step 4.1.e)
            String sqlUpdate = "UPDATE medicine SET quantity_in_stock = quantity_in_stock - ? " +
                               "WHERE medicine_id = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);

            for (CartItem item : cart) {
                // Add to purchase_details
                psDetails.setInt(1, purchaseNo);
                psDetails.setInt(2, item.getMedicineId());
                psDetails.setInt(3, item.getQuantityOrdered());
                psDetails.setDouble(4, item.getDiscount());
                psDetails.setDouble(5, item.getLineTotal());
                psDetails.addBatch();
                
                // Add to medicine update (deducts stock)
                psUpdate.setInt(1, item.getQuantityOrdered());
                psUpdate.setInt(2, item.getMedicineId());
                psUpdate.addBatch();
            }
            
            // Execute all batches
            psDetails.executeBatch(); // This will trigger `prevent_expired_or_discontinued_sale`
            psUpdate.executeBatch();
            
            // If all queries worked, commit the transaction
            con.commit();
            
            return purchaseNo; // Return the new ID for the receipt
            
        } catch (SQLException e) {
            con.rollback(); // ROLLBACK TRANSACTION
            throw e; // Re-throw the exception to notify the panel
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
}