package Controller;

import Model.Purchase;
import Model.PurchaseDetails;
import DB.Database;

import java.sql.*;
import java.util.*;
public class PurchaseController
{
    public Purchase getPurchaseByNo(int pNo) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM purchase WHERE purchase_no=?";
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
    public void addPurchase(Purchase p, PurchaseDetails pd) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql1="INSERT INTO purchase (purchase_no, purchase_date, customer_id)"
        + "VALUES (?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql1);
        pstmt.setInt(1, p.getPurchaseNo());
        pstmt.setDate(2, java.sql.Date.valueOf(p.getPurchaseDate()));
        pstmt.setInt(3, p.getCustomerId());
        pstmt.executeUpdate();

        String sql2="INSERT INTO purchase_details (purchase_no, medicine_id, quantity_ordered, discount, total)"
        + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps=con.prepareStatement(sql2);
        ps.setInt(1, pd.getPurchaseNo());
        ps.setInt(2, pd.getMedicineId());
        ps.setInt(3, pd.getQuantityOrder());
        double discount = (pd.getDiscount() == null) ? 0.0 : pd.getDiscount();
        ps.setDouble(4, discount);  
        ps.setDouble(5, pd.getTotal());
        ps.executeUpdate();

        MedicineController mc= new MedicineController();
        mc.reduceStock(pd.getMedicineId(), pd.getQuantityOrder());
        con.close();
    }
    public void updatePurchase(Purchase p) throws SQLException 
    {
        Connection con = Database.connectdb();
    
        // SQL updates all fields except ID in the row with purchase_no
        String sql = "UPDATE purchase SET purchase_date=?, customer_id=? WHERE purchase_no=?";
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
    public void insertPurchaseDetail(PurchaseDetails pd) throws SQLException
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO purchase_details (purchase_no, medicine_id, quantity_ordered, discount, total)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, pd.getPurchaseNo());
        ps.setInt(2, pd.getMedicineId());
        ps.setInt(3, pd.getQuantityOrder());
        ps.setDouble(4, pd.getDiscount());
        ps.setDouble(5, pd.getTotal());
        ps.executeUpdate();
        con.close();
    }
    public List<PurchaseDetails> getPurchaseDetailsByPurchaseNo(int pNo) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM purchase_details WHERE purchase_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, pNo);
        ResultSet rs = ps.executeQuery();
        List<PurchaseDetails> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new PurchaseDetails(
                rs.getInt("purchase_no"),
                rs.getInt("medicine_id"),
                rs.getInt("quantity_ordered"),
                rs.getDouble("discount"),
                rs.getDouble("total")
            ));
        }   
        con.close();
        return list;
    }
    public int getNextPurchaseNo() throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT COALESCE(MAX(purchase_no), 0) AS max_no FROM purchase";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int next = rs.getInt("max_no") + 1;
        con.close();
        return next;
    }
    public boolean hasDiscount(int customerId) throws SQLException {
        Connection con = Database.connectdb();
        String sql = "SELECT senior_pwd_id FROM customer WHERE customer_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        boolean result = false;
        if (rs.next()) {
            result = rs.getInt("senior_pwd_id") > 0;
        }
        con.close();
        return result;
    }
}
