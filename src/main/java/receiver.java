import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.concurrent.TimeoutException;

public class receiver {
    public static void main(String[] args) {
        ping("mySQL");
        ping("rabbitMQ");
        try {
            mySQL.start("jdbc:mysql://mySQL:3306/","root","1234");
        } catch (SQLException throwables) {
            System.out.println("mySQL connection failed! Cause: " + throwables.getCause().getMessage() + "\nHas it finished starting?");
            throwables.printStackTrace(System.err);
        }
        try {
            rabbitMQ.addSubscription("UpdateLike","Likes",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(message);
                    Events.UpdateLike(json.get("User").toString(),json.get("Post_ID").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestLikesForPost","Likes",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(message);
                    Events.RequestLikesForPost(json.get("Post_ID").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.addSubscription("RequestLikeStatus","Likes",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(message);
                    Events.RequestLikeStatus(json.get("User").toString(),json.get("Post_ID").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            rabbitMQ.setupReceiver("Likes");
        } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public static void ping(String url){
        try {
            InetAddress geek = InetAddress.getByName(url);
            if(geek.isReachable(5000)) System.out.println("Host reached!");
        } catch (IOException e) {
            System.out.println("Sorry ! We can't reach to this host");
        }
    }
}
