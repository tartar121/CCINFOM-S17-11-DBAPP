package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL= "jdbc:mysql://localhost:3306/dbpharmacy";
    private static final String USER= "root";
    private static final String PASSWORD="1234"; // put your MySQL password here

    public static Connection connectdb() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

