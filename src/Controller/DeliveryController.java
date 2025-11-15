package Controller;

import Model.Delivers;
import Model.DeliveryDetails;
import DB.Database;

import java.sql.*;
import java.util.*;
public class DeliveryController {
    public boolean getActive(int supplierId) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT supplier_status FROM supplier WHERE supplier_id = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, supplierId);
        ResultSet rs = pstmt.executeQuery();
        boolean active = false;
        if (rs.next()) {
            active = "Active".equalsIgnoreCase(rs.getString("supplier_status"));
        }
        con.close();
        return active;
    }
    public double getPrice(int medID) throws SQLException
    {
        Connection con = Database.connectdb();
        String sql = "SELECT price_bought FROM medicine WHERE medicine_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, medID);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double price = rs.getDouble("price_bought");
            con.close();
            return price;
        }
        con.close();
        return -1;
    }
    public void addDelivery(Delivers d) throws SQLException
    {
        if (!getActive(d.getsid())) {
            throw new SQLException("Cannot add delivery: Supplier is inactive.");
        }
        Connection con= Database.connectdb();
        String sql= "INSERT INTO delivers (supplier_id, request_date, shipped_date, delivery_status) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt= con.prepareStatement(sql);
        pstmt.setInt(1, d.getsid());
        pstmt.setDate(2, java.sql.Date.valueOf(d.getrdate()));
        if (d.getsdate() != null) pstmt.setDate(3, java.sql.Date.valueOf(d.getsdate()));
        else pstmt.setNull(3, java.sql.Types.DATE);
        if (d.getStatus().isEmpty()) {
            pstmt.setNull(4, java.sql.Types.VARCHAR); // set NULL in DB
        } else {
            pstmt.setString(4, d.getStatus()); // insert actual status
        }
        pstmt.executeUpdate();
        con.close();
    }
    public void updateDelivery(Delivers d) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "UPDATE delivers SET supplier_id=?, request_date=?, shipped_date=?, delivery_status=? WHERE delivery_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
    
        ps.setInt(1, d.getsid());                           // supplier_id
        ps.setDate(2, java.sql.Date.valueOf(d.getrdate())); // request_date
    
        if (d.getsdate() != null)
            ps.setDate(3, java.sql.Date.valueOf(d.getsdate()));
        else
            ps.setNull(3, java.sql.Types.DATE);             // shipped_date
    
        if (d.getStatus().isEmpty()) {
            ps.setNull(4, java.sql.Types.VARCHAR); // set NULL in DB
        } else {
            ps.setString(4, d.getStatus()); // insert actual status
        }                     // delivery_status
        ps.setInt(5, d.getdno());                           // delivery_no (WHERE clause)
    
        ps.executeUpdate();
        con.close();
    }
    public void addDeliveryDetail(DeliveryDetails dd) throws SQLException
    {
        Connection con = Database.connectdb();
        try {
            con.setAutoCommit(false); // ensure both operations succeed or fail together
    
            // 1. Insert delivery detail
            String insertSql = "INSERT INTO delivery_details (delivery_no, medicine_id, quantity, total) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(insertSql);
            insertStmt.setInt(1, dd.getDno());
            insertStmt.setInt(2, dd.getMedid());
            insertStmt.setInt(3, dd.getQuan());
            insertStmt.setDouble(4, dd.getTotal());
            insertStmt.executeUpdate();
    
            // 2. Update medicine stock
            String updateStockSql = "UPDATE medicine SET quantity_in_stock = quantity_in_stock + ? WHERE medicine_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateStockSql);
            updateStmt.setInt(1, dd.getQuan());
            updateStmt.setInt(2, dd.getMedid());

            // 3. Update price bought
             String sql = "UPDATE medicine m " +
                 "JOIN delivery_details dd ON m.medicine_id = dd.medicine_id " +
                 "SET m.price_bought = dd.total / dd.quantity " +
                 "WHERE dd.delivery_no = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, dd.getDno());
            updateStmt.executeUpdate();
    
            con.commit(); // commit both changes
        } catch (SQLException e) {
            con.rollback(); // rollback if anything fails
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
    public List<Delivers> getAllDeliveries() throws SQLException {
        Connection con=Database.connectdb();
        String sql="SELECT * FROM delivers";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Delivers> list = new ArrayList<>();
        while (rs.next()) 
        {
            java.sql.Date shippedDate = rs.getDate("shipped_date"); // may be null
            list.add(new Delivers(
            rs.getInt("delivery_no"),
            rs.getInt("supplier_id"),
            rs.getDate("request_date").toLocalDate(),
            shippedDate != null ? shippedDate.toLocalDate() : null, // only convert if not null
            rs.getString("delivery_status")
            ));
        }
        con.close();
        return list;
    }
    public List<DeliveryDetails> getAllDeliveryDetails() throws SQLException {
        Connection con=Database.connectdb();
        String sql="SELECT * FROM delivery_details";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<DeliveryDetails> list = new ArrayList<>();
        while (rs.next()) 
        {
            list.add(new DeliveryDetails(
            rs.getInt("delivery_no"),
            rs.getInt("medicine_id"),
            rs.getInt("quantity"),
            rs.getDouble("total")
            ));
        }
        con.close();
        return list;
    }
    public Delivers getDeliversByID(int id) throws SQLException
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM delivers WHERE delivery_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            java.sql.Date ship= rs.getDate("shipped_date");
            Delivers d = new Delivers(
                rs.getInt("delivery_no"),
                rs.getInt("supplier_id"),
                rs.getDate("request_date").toLocalDate(),
                ship !=null ? ship.toLocalDate() : null,
                rs.getString("delivery_status")
            );
            con.close();
            return d;
        }
        con.close();
        return null;
    }
    public List<DeliveryDetails> getDeliveryDetailsByDeliveryNo(int deliveryNo) throws SQLException 
    {
        List<DeliveryDetails> list = new ArrayList<>();
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM delivery_details WHERE delivery_no = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, deliveryNo);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            list.add(new DeliveryDetails(
                rs.getInt("delivery_no"),
                rs.getInt("medicine_id"),
                rs.getInt("quantity"),
                rs.getDouble("total")
            ));
        }
        con.close();
        return list;
    }
    public void updateDeliveryDetail(DeliveryDetails dd) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "UPDATE delivery_details SET quantity=?, total=? WHERE delivery_no=? AND medicine_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setInt(1, dd.getQuan());
        ps.setDouble(2, dd.getTotal());
        ps.setInt(3, dd.getDno());
        ps.setInt(4, dd.getMedid());

        String updateStockSql = "UPDATE medicine SET quantity_in_stock = quantity_in_stock + ? WHERE medicine_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateStockSql);
            updateStmt.setInt(1, dd.getQuan());
            updateStmt.setInt(2, dd.getMedid());

            // 3. Update price bought
        String sql1 = "UPDATE medicine m " +
            "JOIN delivery_details dd ON m.medicine_id = dd.medicine_id " +
            "SET m.price_bought = dd.total / dd.quantity " +
            "WHERE dd.delivery_no = ?";
        PreparedStatement pstmt = con.prepareStatement(sql1);
        pstmt.setInt(1, dd.getDno());
        updateStmt.executeUpdate();
    
        ps.executeUpdate();
        con.close();
    }
}
