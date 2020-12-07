import com.auth0.jwt.interfaces.DecodedJWT;
import com.rabbitmq.client.AMQP.BasicProperties;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class receiver {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            mySQL.start(System.getenv("MYSQL_URI"),"root",System.getenv("MYSQL_ROOT_PASSWORD"));
        } catch (SQLException e) {
            System.out.println("mySQL connection failed! Cause: " + e.getMessage() + "\nHas it finished starting?");
            e.printStackTrace(System.err);
        }
        try {
            /* Event: UpdateLike Response: ConfirmLikeUpdate
            *
            * */
            rabbitMQ.addSubscription("UpdateLike","Likes",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    rabbitMQ.send("ConfirmLikeUpdate","",rabbitMQ.setupProperties(correlationID,contentType,400,"Malformed request syntax"));
                    return;
                }
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                DecodedJWT jwt = Security.decodeJWT(token);
                if(jwt == null){
                    rabbitMQ.send("ConfirmLikeUpdate","",rabbitMQ.setupProperties(correlationID,contentType,403,"Invalid Authentication token"));
                    return;
                }
                JSONObject response = Events.UpdateLike(jwt.getSubject(), json.get("post_id"));
                Object like_status = response.get("Like status");
                if(like_status != null) {
                    rabbitMQ.send("ConfirmLikeUpdate",response.toJSONString(),rabbitMQ.setupProperties(correlationID,contentType,200,""));
                } else {
                    rabbitMQ.send("ConfirmLikeUpdate","",rabbitMQ.setupProperties(correlationID,contentType,(Integer) response.get("Status"),(String) response.get("Message")));
                }
            });
            /* Event: RequestLikesForPost Response: ReturnLikesForPost
             *
             * */
            rabbitMQ.addSubscription("RequestLikesForPost","Likes",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    rabbitMQ.send("ReturnLikesForPost","",rabbitMQ.setupProperties(correlationID,contentType,400,"Malformed request syntax"));
                    return;
                }
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                DecodedJWT jwt = Security.decodeJWT(token);
                if(jwt == null){
                    rabbitMQ.send("ReturnLikesForPost","",rabbitMQ.setupProperties(correlationID,contentType,403,"Invalid Authentication token"));
                    return;
                }
                JSONObject response = Events.RequestLikesForPost(json.get("post_id"));
                Object Like_amount = response.get("Like amount");
                if(Like_amount != null) {
                    rabbitMQ.send("ReturnLikesForPost",response.toJSONString(),rabbitMQ.setupProperties(correlationID,contentType,200,""));
                } else {
                    rabbitMQ.send("ReturnLikesForPost","",rabbitMQ.setupProperties(correlationID,contentType,(Integer) response.get("Status"),(String) response.get("Message")));
                }
            });
            /* Event: RequestLikeStatus Response: ReturnLikeStatus
            *
            * */
            rabbitMQ.addSubscription("RequestLikeStatus","Likes",(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                String correlationID = delivery.getProperties().getCorrelationId();
                String contentType = delivery.getProperties().getContentType();
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    System.out.println("The JSON object failed to be parsed! \n"+ message);
                    rabbitMQ.send("ReturnLikeStatus","",rabbitMQ.setupProperties(correlationID,contentType,400,"Malformed request syntax"));
                    return;
                }
                String token = String.valueOf(delivery.getProperties().getHeaders().get("jwt"));
                DecodedJWT jwt = Security.decodeJWT(token);
                if(jwt == null){
                    rabbitMQ.send("ReturnLikeStatus","",rabbitMQ.setupProperties(correlationID,contentType,403,"Invalid Authentication token"));
                    return;
                }
                JSONObject response = Events.RequestLikeStatus(jwt.getSubject(),json.get("post_id"), consumerTag, delivery.getProperties().getCorrelationId());
                Object Like_status = response.get("Like status");
                if(Like_status != null) {
                    rabbitMQ.send("ReturnLikeStatus",response.toJSONString(),rabbitMQ.setupProperties(correlationID,contentType,200,""));
                } else {
                    rabbitMQ.send("ReturnLikeStatus","",rabbitMQ.setupProperties(correlationID,contentType,(Integer) response.get("Status"),(String) response.get("Message")));
                }
            });
            rabbitMQ.setupReceiver("Likes");
            System.out.println("The Likes service is now fully up and running!");
        } catch (IOException | TimeoutException e) {
            System.out.println("Failed to add subscription! Cause: \n" + e.getMessage());
        }
    }
}
