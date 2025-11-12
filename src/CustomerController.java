import java.sql.*;
import java.util.*;
public class CustomerController
{
    public void addCustomer(Customer c) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO customer (customer_name, contact_info, status)"
        + "VALUES (?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);

        pstmt.setString(1, c.getName());
        pstmt.setString(2, c.getContactInfo());
        pstmt.setString(3, c.getStatus());
    }
    public List<Customer> getAllCustomer() throws SQLException
    {
        Connection con=Database.connectdb();
        String sql="SELECT * FROM customer";
        Statement stmt=con.createStatement();
        ResultSet results=stmt.executeQuery(sql); // stores all the result return by the query
        List<Customer> list=new ArrayList<>();
        while (results.next()) {
            list.add(new Customer(
            results.getInt("customer_id"),
            results.getString("customer_name"),
            results.getString("customer_contact_info"),
            results.getInt("senior_pwd_id"),
            results.getString("customer_status")
            ));
        }
        con.close();
        return list;
    }
    public void updateCustomer(Customer c) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="UPDATE customer SET customer_id=?, customer_name=?, customer_contact_info=?, senior_pwd_id=?, customer_status=?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, c.getId());
        pstmt.setString(2, c.getName());
        pstmt.setString(3, c.getContactInfo());
        pstmt.setInt(4, c.getPwdId());
        pstmt.setString(5, c.getStatus());
        con.close();
    }
    public void deleteCustomer(Customer c) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="DELETE FROM customer WHERE customer_id=?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, c.getId());
        pstmt.executeUpdate();
        con.close();
    }
}