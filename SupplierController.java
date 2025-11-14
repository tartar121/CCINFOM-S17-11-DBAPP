import java.sql.*;
import java.util.*;
public class SupplierController
{
    public Supplier getSupplierbyId(int id) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM supplier WHERE supplier_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Supplier s = new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("supplier_address"),
                rs.getString("supplier_contact_info"),
                rs.getString("supplier_status")
            );
            con.close();
            return s;
        }
        con.close();
        return null;
    }
    public void addSupplier(Supplier s) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO supplier (supplier_id, supplier_name, supplier_address, supplier_contact_info, supplier_status)"
        + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, s.getId());
        pstmt.setString(2, s.getName());
        pstmt.setString(3, s.getAddress());
        pstmt.setString(4, s.getContactInfo());
        pstmt.setString(5, s.getStatus());
        pstmt.executeUpdate();
        con.close();
    }
    public List<Supplier> getAllSupplier() throws SQLException
    {
        Connection con=Database.connectdb();
        String sql="SELECT * FROM supplier";
        Statement stmt=con.createStatement();
        ResultSet results=stmt.executeQuery(sql); // stores all the result return by the query
        List<Supplier> list=new ArrayList<>();
        while (results.next()) {
            list.add(new Supplier(
            results.getInt("supplier_id"),
            results.getString("supplier_name"),
            results.getString("supplier_address"),
            results.getString("supplier_contact_info"),
            results.getString("supplier_status")
            ));
        }
        con.close();
        return list;
    }
    public void updateSupplier(Supplier s) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="UPDATE supplier SET supplier_name=?, supplier_address, supplier_contact_info=?, supplier_status=? WEHRE supplier_id=?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setString(1, s.getName());
        pstmt.setString(2, s.getAddress());
        pstmt.setString(3, s.getContactInfo());
        pstmt.setString(4, s.getStatus());
        pstmt.executeUpdate();
        con.close();
    }
}