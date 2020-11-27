import java.sql.*;

public class mySQL {
    private static Connection con;
    private static Statement stmt;

    public static void start(String URL, String User, String psw) throws SQLException {
        con = DriverManager.getConnection(URL, User, psw);
        stmt = con.createStatement();
    }

    public static void stop() {
        try {
            System.out.println("mySQL connection shutting down");
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean changeLike(String UserID, String PostID) throws SQLException{
        String sql;
        int affected;
        Boolean bool = checkLike(UserID,PostID);
        if(bool) {
            System.out.println("Exists!");
            sql = String.format("DELETE FROM Likes WHERE postID='%s' AND userID='%s'", PostID, UserID);
            affected = stmt.executeUpdate(sql);
            if(affected == 1)
                return false;
        } else {
            System.out.println("Does not!");
            sql = String.format("INSERT INTO Likes (postID, userID) VALUES ('%s','%s');",PostID,UserID);
            affected = stmt.executeUpdate(sql);
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