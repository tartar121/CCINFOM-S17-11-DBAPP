import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL= "jdbc:mysql://localhost:3306/dbpharmacy";
    private static final String USER= "root";
    private static final String PASSWORD="February23,2006()";

    public static Connection connectdb() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

