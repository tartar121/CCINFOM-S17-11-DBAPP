package Controller;

import Model.Return;
import Model.ReturnDetailsDisplay; // <-- 1. IMPORT THE NEW HELPER
import DB.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReturnController {
    // ... (toSqlDate and toLocalDate helpers stay the same) ...

    private java.sql.Date toSqlDate(LocalDate date) {
        return (date == null) ? null : java.sql.Date.valueOf(date);
    }
    private LocalDate toLocalDate(java.sql.Date date) {
        return (date == null) ? null : date.toLocalDate();
    }

    public Return getReturnByNo(int rNo) throws SQLException {
        // ... (This whole method stays the same) ...
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
                rs.getDate("request_date").toLocalDate(),
                rs.getDate("shipped_date").toLocalDate(),
                rs.getString("return_status")
            );
            con.close();
            return r;
        }
        con.close();
        return null;
    }

    // ... (addReturn and updateReturn methods stay the same) ...
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

    public void updateReturn(Return r) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "UPDATE `return` SET supplier_id=?, reason=?, request_date=?, shipped_date=?, return_status=?"
                   + " WHERE return_no=?";
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, r.getSupplierId());
        ps.setString(2, r.getReason());
        ps.setDate(3, toSqlDate(r.getRequestDate())); // Fixed a typo here (was 4)
        ps.setDate(4, toSqlDate(r.getShippedDate())); // Fixed a typo here (was 5)
        ps.setString(5, r.getReturnStatus());
        ps.setInt(6, r.getReturnNo()); // WHERE clause
        
        ps.executeUpdate();
        con.close();
    }

    public List<Return> getAllReturns() throws SQLException {
        // ... (This whole method stays the same) ...
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
    
    // ===== 2. ADD THIS NEW METHOD =====
    /**
     * This gets the "details" for a specific return,
     * fulfilling the "view details" requirement.
     */
    public List<ReturnDetailsDisplay> getDetailsForReturn(int returnNo) throws SQLException {
        List<ReturnDetailsDisplay> details = new ArrayList<>();
        Connection con = Database.connectdb();
        
        // This query JOINS return_details with medicine to get the name
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