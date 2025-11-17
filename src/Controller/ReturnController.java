package Controller;

import Model.Return;
import Model.ReturnDetailsDisplay;
import DB.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReturnController {

    private java.sql.Date toSqlDate(LocalDate date) {
        return (date == null) ? null : java.sql.Date.valueOf(date);
    }
    private LocalDate toLocalDate(java.sql.Date date) {
        return (date == null) ? null : date.toLocalDate();
    }

    public Return getReturnByNo(int rNo) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM `return` WHERE return_no=?"; 
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, rNo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Return r = new Return(
                rs.getInt("return_no"),
                rs.getInt("supplier_id"),
                rs.getString("reason"),
                toLocalDate(rs.getDate("request_date")),
                toLocalDate(rs.getDate("shipped_date")),
                rs.getString("return_status")
            );
            con.close();
            return r;
        }
        con.close();
        return null;
    }

    // This is the simple "Admin" Add.
    public void addReturn(Return r) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "INSERT INTO `return` (return_no, supplier_id, reason, request_date, shipped_date, return_status)"
                   + " VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, r.getReturnNo());
        pstmt.setInt(2, r.getSupplierId());
        pstmt.setString(3, r.getReason());
        pstmt.setDate(4, toSqlDate(r.getRequestDate()));
        pstmt.setDate(5, toSqlDate(r.getShippedDate()));
        pstmt.setString(6, r.getReturnStatus());
        
        pstmt.executeUpdate();
        con.close();
    }

    /**
     * This is the "Admin" 🗄️ update. This is where the status
     * gets changed from 'Requested' to 'Returned' or 'Cancelled'.
     * THIS IS THE FIX.
     */
    public void updateReturn(Return r, String oldStatus) throws SQLException {
        Connection con = Database.connectdb();
        con.setAutoCommit(false); // Start a transaction

        try {
            // 1. Update the main 'return' record
            String sql = "UPDATE `return` SET supplier_id=?, reason=?, request_date=?, shipped_date=?, return_status=? WHERE return_no=?";
            PreparedStatement ps = con.prepareStatement(sql);
    
            ps.setInt(1, r.getSupplierId());
            ps.setString(2, r.getReason());
            ps.setDate(3, toSqlDate(r.getRequestDate()));
            ps.setDate(4, toSqlDate(r.getShippedDate()));
            ps.setString(5, r.getReturnStatus());
            ps.setInt(6, r.getReturnNo());
    
            ps.executeUpdate();

            // 2. --- THIS IS THE CRITICAL LOGIC ---
            // If the Admin just set the status to 'Returned'
            // (and it was 'Requested' before), we remove the stock.
            if ("Returned".equals(r.getReturnStatus()) && "Requested".equals(oldStatus)) {
                
                // This query finds all medicine batches in 'return_details'
                // and sets their stock to 0.
                String sqlUpdateStock = "UPDATE medicine m " +
                                  "JOIN return_details rd ON m.medicine_id = rd.medicine_id " +
                                  "SET m.quantity_in_stock = 0 " +
                                  "WHERE rd.return_no = ?";
                
                PreparedStatement psUpdateStock = con.prepareStatement(sqlUpdateStock);
                psUpdateStock.setInt(1, r.getReturnNo());
                psUpdateStock.executeUpdate();
            }
            
            con.commit(); // Commit all changes

        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    public List<Return> getAllReturns() throws SQLException {
        List<Return> returns = new ArrayList<>();
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM `return`";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Return r = new Return(
                rs.getInt("return_no"),
                rs.getInt("supplier_id"),
                rs.getString("reason"),
                toLocalDate(rs.getDate("request_date")),
                toLocalDate(rs.getDate("shipped_date")),
                rs.getString("return_status")
            );
            returns.add(r);
        }
        con.close();
        return returns;
    }
    
    /**
     * This gets the "details" for a specific return,
     * fulfilling the "view details" requirement.
     */
    public List<ReturnDetailsDisplay> getDetailsForReturn(int returnNo) throws SQLException {
        List<ReturnDetailsDisplay> details = new ArrayList<>();
        Connection con = Database.connectdb();
        
        String sql = "SELECT rd.medicine_id, m.medicine_name, rd.quantity_returned, rd.price_returned " +
                     "FROM return_details rd " +
                     "JOIN medicine m ON rd.medicine_id = m.medicine_id " +
                     "WHERE rd.return_no = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, returnNo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            details.add(new ReturnDetailsDisplay(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getInt("quantity_returned"),
                rs.getDouble("price_returned")
            ));
        }
        con.close();
        return details;
    }
}