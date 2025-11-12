import java.sql.*;
import java.util.*;
public class MedicineController
{
    public void addMedicine(Medicine m) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO medicine (medicine_id, medicine_name, price_bought, price_for_sale, quantity_in_stock, expiration_date, discontinued)"
        + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);
        // we don't need medicine_id because it auto_increments
        pstmt.setInt(1, m.getId());
        pstmt.setString(1, m.getName());
        pstmt.setDouble(2, m.getPriceBought());
        pstmt.setDouble(3, m.getPriceForSale());
        pstmt.setInt(4, m.getQuantity());
        pstmt.setDate(5, java.sql.Date.valueOf(m.getExpirationDate()));
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