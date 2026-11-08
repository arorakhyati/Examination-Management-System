import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    static final String URL = "jdbc:mysql://localhost:3306/examdb?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASSWORD = "";

    public static Connection getConnection() throws Exception {
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
        return con;
    }

    public static void main(String[] args) throws Exception {
        getConnection();
        System.out.println("Connection successful!");
    }
}
