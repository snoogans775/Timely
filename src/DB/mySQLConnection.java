package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author kevin
 */
public class mySQLConnection {
    private static final String databaseName = "U06ofX";
    private static final String DB_URL = "jdbc:mysql://3.227.166.251/" + databaseName;
    private static final String DB_USER = "U06ofX";
    private static final String DB_PASS = "53688825208";
    private static final String driver = "com.mysql.jdbc.Driver";
    
    public static Connection conn;

    
    //BEGIN METHODS
    
    public static void makeConnection() throws ClassNotFoundException, SQLException {
            Class.forName(driver);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            System.out.println("Started New Connection: "+ conn);
    }

    public static void closeConnection() throws SQLException {
        conn.close();

        System.out.println("Closed:" + conn);
    }
    
    //BEGIN GETTERS AND SETTERS
    
    public static Connection getConnection() {
        return conn;
    }

}
