package Controller;

import java.sql.*;
import java.util.*;

import DB.Database;
import Model.ProcurementReport;

public class ProcurementReportController {
    public List<ProcurementReport> getProcurementReport(int month, int year) throws SQLException{
        List<ProcurementReport> report= new ArrayList<>();
        Connection con=Database.connectdb();
        
        String sql= 
        "SELECT s.supplier_id, s.supplier_name, " +
        "COUNT(DISTINCT d.delivery_no) AS numRestocks, " +
        "SUM(dd.total) AS totalAmount " +
        "FROM supplier s " +
        "JOIN delivers d ON s.supplier_id = d.supplier_id " +
        "JOIN delivery_details dd ON d.delivery_no = dd.delivery_no " +
        "WHERE MONTH(d.request_date) = ? AND YEAR(d.request_date) = ? " +
        "GROUP BY s.supplier_id, s.supplier_name";

        PreparedStatement ps=con.prepareStatement(sql);
        ps.setInt(1, month);
        ps.setInt(2, year);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            report.add(new ProcurementReport(
            rs.getInt("supplier_id"),
            rs.getString("supplier_name"),
            rs.getInt("numRestocks"),
            rs.getDouble("totalAmount")
        ));
        }

        con.close();
        return report;

    }
    
}
