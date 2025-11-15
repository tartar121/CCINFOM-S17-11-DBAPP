package Controller;

import Model.Purchase;
import DB.Database;

import java.sql.*;
import java.util.*;
public class PurchaseController
{
    public Purchase getPurchaseByNo(int pNo) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM purchase WHERE purchase_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, pNo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Purchase p = new Purchase(
                rs.getInt("purchase_no"),
                rs.getDate("purchase_date").toLocalDate(),
                rs.getInt("customer_id")
            );
            con.close();
            return p;
        }
        con.close();
        return null;
    }
    public void addPurchase(Purchase p) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO purchase (purchase_no, purchase_date, customer_id)"
        + "VALUES (?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, p.getPurchaseNo());
        pstmt.setDate(2, java.sql.Date.valueOf(p.getPurchaseDate()));
        pstmt.setInt(3, p.getCustomerId());
        pstmt.executeUpdate();
        con.close();
    }
    public void updatePurchase(Purchase p) throws SQLException 
    {
        Connection con = Database.connectdb();
    
        // SQL updates all fields except ID in the row with purchase_id
        String sql = "UPDATE purchase SET puchase_date=?, customer_id=? WHERE purchase_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
    
        ps.setDate(1, java.sql.Date.valueOf(p.getPurchaseDate()));
        ps.setInt(2, p.getCustomerId());
    
        ps.executeUpdate();
        con.close();
    }

    public Purchase getPurchase(int pNo) throws SQLException {
        String sql = "SELECT * FROM purchase WHERE purchase_no=?";
        try (Connection con = Database.connectdb();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Purchase(
                        rs.getInt("purchase_no"),
                        rs.getDate("purchase_date").toLocalDate(),
                        rs.getInt("customer_id")
                );
            }
        }
        return null;
    }
    public List<Purchase> getAllPurchases() throws SQLException
    {
        Connection con=Database.connectdb();
        String sql="SELECT * FROM purchase";
        Statement stmt=con.createStatement();
        ResultSet results=stmt.executeQuery(sql); // stores all the result return by the query
        List<Purchase> list=new ArrayList<>();
        while (results.next()) {
            list.add(new Purchase(
            results.getInt("purchase_no"),
            results.getDate("purchase_date").toLocalDate(),
            results.getInt("customer_id")
            ));
        }
        con.close();
        return list;
    }
}