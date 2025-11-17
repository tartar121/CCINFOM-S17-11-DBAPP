package Controller;

import Model.Purchase;
import Model.PurchaseDetails;
import Model.PurchaseDetailsDisplay; // Helper for "View Details"
import DB.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Controller for Purchases
 * Simple CRUD on the 'purchase' and 'purchase_details' tables
 * User transaction logic (checking stock, discounts) is in 'CreatePurchaseController'
 */
public class PurchaseController {

    private java.sql.Date toSqlDate(LocalDate date) {
        return (date == null) ? null : java.sql.Date.valueOf(date);
    }
    private LocalDate toLocalDate(java.sql.Date date) {
        return (date == null) ? null : date.toLocalDate();
    }

    public Purchase getPurchaseByNo(int pNo) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM purchase WHERE purchase_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, pNo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Purchase p = new Purchase(
                rs.getInt("purchase_no"),
                toLocalDate(rs.getDate("purchase_date")),
                rs.getInt("customer_id")
            );
            con.close();
            return p;
        }
        con.close();
        return null;
    }

    // Add
    public void addPurchase(Purchase p) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "INSERT INTO purchase (purchase_no, purchase_date, customer_id)"
                   + "VALUES (?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, p.getPurchaseNo());
        pstmt.setDate(2, toSqlDate(p.getPurchaseDate()));
        pstmt.setInt(3, p.getCustomerId());
        pstmt.executeUpdate();
        con.close();
    }

    // Update
    public void updatePurchase(Purchase p) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "UPDATE purchase SET purchase_date=?, customer_id=? WHERE purchase_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setDate(1, toSqlDate(p.getPurchaseDate()));
        ps.setInt(2, p.getCustomerId());
        ps.setInt(3, p.getPurchaseNo());
        ps.executeUpdate();
        con.close();
    }

    public List<Purchase> getAllPurchases() throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM purchase";
        Statement stmt = con.createStatement();
        ResultSet results = stmt.executeQuery(sql);
        List<Purchase> list = new ArrayList<>();
        while (results.next()) {
            list.add(new Purchase(
                results.getInt("purchase_no"),
                toLocalDate(results.getDate("purchase_date")),
                results.getInt("customer_id")
            ));
        }
        con.close();
        return list;
    }

    /**
     * This gets the "details" for a specific purchase for the Admin panel,
     * including the medicine name.
     */
    public List<PurchaseDetailsDisplay> getDetailsForPurchase(int purchaseNo) throws SQLException {
        List<PurchaseDetailsDisplay> details = new ArrayList<>();
        Connection con = Database.connectdb();
        
        String sql = "SELECT pd.medicine_id, m.medicine_name, pd.quantity_ordered, pd.discount, pd.total " +
                     "FROM purchase_details pd " +
                     "JOIN medicine m ON pd.medicine_id = m.medicine_id " +
                     "WHERE pd.purchase_no = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, purchaseNo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            details.add(new PurchaseDetailsDisplay(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getInt("quantity_ordered"),
                rs.getDouble("discount"),
                rs.getDouble("total")
            ));
        }
        con.close();
        return details;
    }
}