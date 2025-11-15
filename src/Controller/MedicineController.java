package Controller;

import Model.Medicine;
import DB.Database;

import java.sql.*;
import java.util.*;
public class MedicineController
{
    public Medicine getMedicineById(int id) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM medicine WHERE medicine_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
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
            return m;
        }
        con.close();
        return null;
    }
    public void addMedicine(Medicine m) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO medicine (medicine_id, medicine_name, price_bought, price_for_sale, quantity_in_stock, expiration_date, discontinued)"
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, m.getId());
        pstmt.setString(2, m.getName());
        pstmt.setDouble(3, m.getPriceBought());
        pstmt.setDouble(4, m.getPriceForSale());
        pstmt.setInt(5, m.getQuantity());
        pstmt.setDate(6, java.sql.Date.valueOf(m.getExpirationDate()));
        pstmt.setBoolean(7, m.isDiscontinued());
        pstmt.executeUpdate();
        con.close();
    }
    public void updateMedicine(Medicine m) throws SQLException 
    {
        Connection con = Database.connectdb();
    
        // SQL updates all fields except ID in the row with medicine_id
        String sql = "UPDATE medicine SET medicine_name=?, price_bought=?, price_for_sale=?, " +
                     "quantity_in_stock=?, expiration_date=?, discontinued=? WHERE medicine_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
    
        ps.setString(1, m.getName());
        ps.setDouble(2, m.getPriceBought());
        ps.setDouble(3, m.getPriceForSale());
        ps.setInt(4, m.getQuantity());
        ps.setDate(5, java.sql.Date.valueOf(m.getExpirationDate()));
        ps.setBoolean(6, m.isDiscontinued());
        ps.setInt(7, m.getId());
    
        ps.executeUpdate();
        con.close();
    }

    public Medicine getMedicine(int id) throws SQLException {
        String sql = "SELECT * FROM medicine WHERE medicine_id=?";
        try (Connection con = Database.connectdb();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getDouble("price_bought"),
                        rs.getDouble("price_for_sale"),
                        rs.getInt("quantity_in_stock"),
                        rs.getDate("expiration_date").toLocalDate(),
                        rs.getBoolean("discontinued")
                );
            }
        }
        return null;
    }
    public List<Medicine> getAllMedicines() throws SQLException
    {
        Connection con=Database.connectdb();
        String sql="SELECT * FROM medicine";
        Statement stmt=con.createStatement();
        ResultSet results=stmt.executeQuery(sql); // stores all the result return by the query
        List<Medicine> list=new ArrayList<>();
        while (results.next()) {
            list.add(new Medicine(
            results.getInt("medicine_id"),
            results.getString("medicine_name"),
            results.getDouble("price_bought"),
            results.getDouble("price_for_sale"),
            results.getInt("quantity_in_stock"),
            results.getDate("expiration_date").toLocalDate(),
            results.getBoolean("discontinued")
            ));
        }
        con.close();
        return list;
    }
}