package Controller;

import Model.MedicineReturnReport;
import DB.Database;
import java.sql.*;
import java.util.*;

public class MedicineReturnReportController {
    
    public List<MedicineReturnReport> getMedicineReturnReport(int month, int year) throws SQLException
    {
        List<MedicineReturnReport> list= new ArrayList<>();
        Connection con= Database.connectdb();
        String sql = """
        SELECT 
            m.medicine_id,
            m.medicine_name,
            COUNT(DISTINCT rd.return_no) AS number_of_returns,
            SUM(rd.quantity_returned) AS total_quantity_returned,
            SUM(rd.price_returned * rd.quantity_returned) AS total_amount_returned
            FROM return_details rd
            JOIN `return` r ON rd.return_no = r.return_no
            JOIN medicine m ON rd.medicine_id = m.medicine_id
            WHERE MONTH(r.request_date) = ?
            AND YEAR(r.request_date) = ?
            GROUP BY m.medicine_id, m.medicine_name
            ORDER BY total_amount_returned DESC
        """;
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, month);
        ps.setInt(2, year);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new MedicineReturnReport(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getInt("number_of_returns"),
                rs.getInt("total_quantity_returned"),
                rs.getDouble("total_amount_returned")
            ));
        }

        return list;
    }
}
