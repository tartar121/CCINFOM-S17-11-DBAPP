import java.sql.*;
import java.util.*;
public class CustomerController
{
    public Customer getCustomerbyId(int id) throws SQLException 
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM customer WHERE customer_id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Customer c = new Customer(
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_contact_info"),
                rs.getInt("senior_pwd_id"),
                rs.getString("customer_status")
            );
            con.close();
            return c;
        }
        con.close();
        return null;
    }
    public void addCustomer(Customer c) throws SQLException 
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO customer (customer_id, customer_name, customer_contact_info, senior_pwd_id, customer_status)"
        + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);

        pstmt.setInt(1, c.getId());
        pstmt.setString(2, c.getName());
        pstmt.setString(3, c.getContactInfo());
        if (c.getPwdId() == 0) {
        pstmt.setNull(4, java.sql.Types.INTEGER); 
        } else {
        pstmt.setInt(4, c.getPwdId());
        }
        pstmt.setString(5, c.getStatus());
        pstmt.executeUpdate();
        con.close();
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
        String sql="UPDATE customer SET customer_name=?, customer_contact_info=?, senior_pwd_id=?, customer_status=? WHERE customer_id?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setString(1, c.getName());
        pstmt.setString(2, c.getContactInfo());
        if (c.getPwdId() == 0) {
            pstmt.setNull(3, java.sql.Types.INTEGER);
        } else {
            pstmt.setInt(3, c.getPwdId());
        }
        pstmt.setInt(3, c.getPwdId());
        pstmt.setString(4, c.getStatus());
        //pstmt.setInt(5, c.getId());
        pstmt.executeUpdate();
        con.close();
    }
}