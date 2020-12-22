import java.sql.*;

public class mySQL {
    private static Connection con;
    private static Statement stmt;

    public static void start(String URL, String User, String psw) throws SQLException {
        con = DriverManager.getConnection(URL, User, psw);
        stmt = con.createStatement();
        System.out.println("MySQL connection is now ready!");
    }

    public static void stop() {
        try {
            System.out.println("mySQL connection shutting down");
            con.close();
        } catch (SQLException e) {
            System.out.println("An error occured while stopping the mySQL connection. Cause: " + e.getCause().getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static boolean changeLike(String UserID, String PostID) throws SQLException{
        String sql;
        int affected;
        Boolean bool = checkLike(UserID,PostID);
        if(bool) {
            System.out.println("Setting like status to false!");
            sql = String.format("DELETE FROM Likes WHERE postID='%s' AND userID='%s'", PostID, UserID);
            affected = stmt.executeUpdate(sql);
            if(affected == 1)
                return false;
        } else {
            System.out.println("Setting like status to true");
            sql = String.format("INSERT INTO Likes (postID, userID) VALUES ('%s','%s');",PostID,UserID);
            affected = stmt;
            if(affected == 1)
                return true;
        }
        throw new SQLException("Only a single row is expected to affected! Rows affected: " + affected);
    }

    public static int LikesForPost(String PostID) throws SQLException {
        String sql = String.format("SELECT Count(*) FROM Likes WHERE postID='%s'",PostID);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getInt(1);
    }

    public static boolean checkLike(String UserID, String PostID) throws SQLException {
        String sql = String.format("SELECT * FROM Likes WHERE postID='%s' AND userID='%s'",PostID,UserID);
        ResultSet rs = stmt.executeQuery(sql);
        return rs.next();
    }
}
