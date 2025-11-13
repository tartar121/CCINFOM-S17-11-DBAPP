import java.sql.*;
import java.util.*;

public class ReturnController {
    public Return getReturnByNo(int rNo) throws SQLException
    {
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM returns WHERE return_no=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, rNo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Return r = new Return(
                rs.getInt("return_no"),
                rs.getInt("supplier_id"),
                rs.getString("reason"),
                rs.getDate("request_date").toLocalDate(),
                rs.getDate("shipped_date").toLocalDate(),
                rs.getString("return_status")
            );
            con.close();
            return r;
        }
        con.close();
        return null;
    }

    public void addReturn(Return r) throws SQLException
    {
        Connection con=Database.connectdb();
        String sql="INSERT INTO returns (return_no, supplier_id, reason, request_date, shipped_date, return_status)"
        + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, r.getReturnNo());
        pstmt.setInt(2, r.getSupplierId());
        pstmt.setString(3, r.getReason());
        pstmt.setDate(4, java.sql.Date.valueOf(r.getRequestDate()));
        pstmt.setDate(5, java.sql.Date.valueOf(r.getShippedDate()));
        pstmt.setString(6, r.getReturnStatus());
        pstmt.executeUpdate();
        con.close();
    }

    public void updateReturn(Return r) throws SQLException
    {
        Connection con = Database.connectdb();

        // SQL updates all fields except ID in the row with return_no
        String sql = "UPDATE returns SET supplier_id=?, reason=?, request_date=?, shipped_date=?, return_status=? WHERE return_no=?";
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, r.getSupplierId());
        ps.setString(2, r.getReason());
        ps.setDate(3, java.sql.Date.valueOf(r.getRequestDate()));
        ps.setDate(4, java.sql.Date.valueOf(r.getShippedDate()));
        ps.setString(5, r.getReturnStatus());
        ps.setInt(6, r.getReturnNo());

        ps.executeUpdate();
        con.close();
    }

    public List<Return> getAllReturns() throws SQLException
    {
        List<Return> returns = new ArrayList<>();
        Connection con = Database.connectdb();
        String sql = "SELECT * FROM returns";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Return r = new Return(
                rs.getInt("return_no"),
                rs.getInt("supplier_id"),
                rs.getString("reason"),
                rs.getDate("request_date").toLocalDate(),
                rs.getDate("shipped_date").toLocalDate(),
                rs.getString("return_status")
            );
            returns.add(r);
        }
        con.close();
        return returns;
    }
}
