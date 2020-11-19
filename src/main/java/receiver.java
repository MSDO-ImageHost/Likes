import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class receiver {
    public static void main(String[] args) {
        mySQL.start("jdbc:mysql://localhost:3306","root","1234");
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
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
