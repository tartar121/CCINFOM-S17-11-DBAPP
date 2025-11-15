package Controller;

import java.sql.*;
import java.util.*;

import DB.Database;
import Model.CustomerPurchaseReport;

public class CustomerPurchaseReportController {
    public List<CustomerPurchaseReport> getCusomerPurchaseReport(int month, int year) throws SQLException{
        List<CustomerPurchaseReport> report= new ArrayList<>();
        Connection con=Database.connectdb();
        
        String sql= 
        "SELECT c.customer_id, c.customer_name, " +
        "COUNT(DISTINCT p.purchase_no) AS numSelling, " +
        "SUM(pd.total) AS totalAmount " +
        "FROM customer c " +
        "JOIN purchase p ON c.customer_id = p.customer_id " +
        "JOIN purchase_details pd ON p.purchase_no = pd.purchase_no " +
        "WHERE MONTH(p.purchase_date) = ? AND YEAR(p.purchase_date) = ? " +
        "GROUP BY c.customer_id, c.customer_name";

        PreparedStatement ps=con.prepareStatement(sql);
        ps.setInt(1, month);
        ps.setInt(2, year);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            report.add(new CustomerPurchaseReport(
            rs.getInt("customer_id"),
            rs.getString("customer_name"),
            rs.getInt("numSelling"),
            rs.getDouble("totalAmount")
        ));
        }

        con.close();
        return report;

    }
    
}
