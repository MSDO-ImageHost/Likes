import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Events {
    public static void UpdateLike(String User_ID, String Post_ID){
        try {
            System.out.println("User_ID: " + User_ID + " Post_ID: " + Post_ID);
            Boolean bool = mySQL.changeLike(User_ID,Post_ID);
            JSONObject json = new JSONObject();
            json.put("Like status", bool);
            rabbitMQ.send("ConfirmLikeUpdate",json.toString());
        } catch (SQLException e) {
            System.out.println("An error occured in the SQL connection");
            System.out.println(e.getMessage()); //Perhaps log this error somewhere?
        } catch (TimeoutException | IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            System.out.println("An error occured in the RabbitMQ connection");
            System.out.println(e.getMessage()); //Perhaps log this error somewhere?
        }
    }

    public static void RequestLikesForPost(String Post_ID){
        try {
            System.out.println("Post_ID: " + Post_ID);
            int count = mySQL.LikesForPost(Post_ID);
            JSONObject json = new JSONObject();
            json.put("Like amount", count);
            System.out.println(count);
            rabbitMQ.send("ReturnLikesForPost",json.toString());
        } catch (SQLException e) {
            System.out.println("An error occured in the SQL connection");
            System.out.println(e.getMessage()); //Perhaps log this error somewhere?
        } catch (TimeoutException | IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            System.out.println("An error occured in the RabbitMQ connection");
            System.out.println(e.getMessage()); //Perhaps log this error somewhere?
        }
    }
    public static void RequestLikeStatus(String User_ID, String Post_ID){
        try {
            System.out.println("User_ID: " + User_ID + " Post_ID: " + Post_ID);
            Boolean bool = mySQL.checkLike(User_ID,Post_ID);
            JSONObject json = new JSONObject();
            json.put("Like status", bool);
            System.out.println(bool);
            rabbitMQ.send("ReturnLikeStatus",json.toString());
        } catch (SQLException e) {
            System.out.println("An error occured in the SQL connection");
            System.out.println(e.getMessage()); //Perhaps log this error somewhere?
        } catch (TimeoutException | IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            System.out.println("An error occured in the RabbitMQ connection");
            System.out.println(e.getMessage()); //Perhaps log this error somewhere?
        }
    }
}
