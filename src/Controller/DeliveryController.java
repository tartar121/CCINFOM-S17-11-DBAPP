package Controller;

import Model.Delivers;
import Model.DeliveryDetails;
import Model.DeliveryDetailsDisplay;
import DB.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DeliveryController {

    // Helper to handle null dates (local date to sql date)
    private java.sql.Date toSqlDate(LocalDate date) {
        return (date == null) ? null : java.sql.Date.valueOf(date);
    }
    // sql date to local date
    private LocalDate toLocalDate(java.sql.Date date) {
        return (date == null) ? null : date.toLocalDate();
    }

    public Delivers getDeliversByID(int dNo) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM delivers WHERE delivery_no = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, dNo);
        ResultSet rs = ps.executeQuery();
        
        Delivers d = null;
        if (rs.next()) {
            d = new Delivers(
                rs.getInt("delivery_no"),
                rs.getInt("supplier_id"),
                toLocalDate(rs.getDate("request_date")),
                toLocalDate(rs.getDate("shipped_date")),
                rs.getString("delivery_status")
            );
        }
        con.close();
        return d;
    }

    // Admin Add
    public void addDelivery(Delivers d) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "INSERT INTO delivers (delivery_no, supplier_id, request_date, shipped_date, delivery_status)"
                   + " VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        
        //Admin manually set the delivery_no in this panel
        pstmt.setInt(1, d.getdno()); 
        pstmt.setInt(2, d.getsid());
        pstmt.setDate(3, toSqlDate(d.getrdate()));
        pstmt.setDate(4, toSqlDate(d.getsdate()));
        pstmt.setString(5, d.getStatus());
        
        pstmt.executeUpdate();
        con.close();
    }

    /**
     * Admin Update
     * gets changed from 'Delivered' or 'Cancelled'.
     */
    public void updateDelivery(Delivers d) throws SQLException {
        Connection con = Database.connectdb();
        con.setAutoCommit(false); // Start a transaction
        
        try {
            // Update the main 'delivers' record
            String sql = "UPDATE delivers SET supplier_id=?, request_date=?, shipped_date=?, delivery_status=?"
                       + " WHERE delivery_no=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, d.getsid());
            ps.setDate(2, toSqlDate(d.getrdate()));
            ps.setDate(3, toSqlDate(d.getsdate()));
            ps.setString(4, d.getStatus());
            ps.setInt(5, d.getdno()); // WHERE clause
            ps.executeUpdate();

            // If Admin set the status to 'Delivered',
            // we must now update the stock for all items in that delivery.
            if ("Delivered".equals(d.getStatus())) {
                
                // This query finds all items in delivery_details and their requested quantity,
                // and adds that quantity to the 'medicine' table
                String sqlUpdateStock = "UPDATE medicine m " +
                                  "JOIN delivery_details dd ON m.medicine_id = dd.medicine_id " +
                                  "SET m.quantity_in_stock = m.quantity_in_stock + dd.quantity " +
                                  "WHERE dd.delivery_no = ?";
                
                PreparedStatement psUpdateStock = con.prepareStatement(sqlUpdateStock);
                psUpdateStock.setInt(1, d.getdno());
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

    public List<Delivers> getAllDeliveries() throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM delivers";
        Statement stmt = con.createStatement();
        ResultSet results = stmt.executeQuery(sql);
        
        List<Delivers> list = new ArrayList<>();
        while (results.next()) {
            list.add(new Delivers(
                results.getInt("delivery_no"),
                results.getInt("supplier_id"),
                toLocalDate(results.getDate("request_date")),
                toLocalDate(results.getDate("shipped_date")),
                results.getString("delivery_status")
            ));
        }
        con.close();
        return list;
    }
    
    /**
     * This gets the "details" for a specific delivery for the Admin panel,
     * including the medicine name.
     */
    public List<DeliveryDetailsDisplay> getDetailsForDelivery(int deliveryNo) throws SQLException {
        List<DeliveryDetailsDisplay> details = new ArrayList<>();
        Connection con = Database.connectdb();
        
        String sql = "SELECT dd.medicine_id, m.medicine_name, dd.quantity, dd.total " +
                     "FROM delivery_details dd " +
                     "JOIN medicine m ON dd.medicine_id = m.medicine_id " +
                     "WHERE dd.delivery_no = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, deliveryNo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            details.add(new DeliveryDetailsDisplay(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getInt("quantity"),
                rs.getDouble("total")
            ));
        }
        con.close();
        return details;
    }
}