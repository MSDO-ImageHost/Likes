import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class receiver {
    public static void main(String[] args) {
        ping("rabbitmq:5672");
        String mySQLURI = "jdbc:mysql://" + System.getenv("MYSQL_HOST") + ":" + System.getenv("MYSQL_PORT") + "/" + System.getenv("MYSQL_DB");
        try {
            mySQL.start(mySQLURI,System.getenv("MYSQL_USER"),System.getenv("MYSQL_ROOT_PASSWORD"));
            rabbitMQ.setupRabbit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        JSONParser parser = new JSONParser();
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
                Object like_status = response.get("like_status");
                if(like_status != null) {
                    rabbitMQ.send("ConfirmLikeUpdate",response.toJSONString(),rabbitMQ.setupProperties(correlationID,contentType,200,""));
                } else {
                    rabbitMQ.send("ConfirmLikeUpdate","",rabbitMQ.setupProperties(correlationID,contentType,(Integer) response.get("Status"),(String) response.get("Message")));
                    if(((Integer) response.get("Status")) == 503){
                        System.exit(-1);
                    }
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
                JSONObject response = Events.RequestLikesForPost(json.get("post_id"));
                Object Like_amount = response.get("like_amount");
                if(Like_amount != null) {
                    rabbitMQ.send("ReturnLikesForPost",response.toJSONString(),rabbitMQ.setupProperties(correlationID,contentType,200,""));
                } else {
                    rabbitMQ.send("ReturnLikesForPost","",rabbitMQ.setupProperties(correlationID,contentType,(Integer) response.get("Status"),(String) response.get("Message")));
                    if(((Integer) response.get("Status")) == 503){
                        System.exit(-1);
                    }
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
                JSONObject response = Events.RequestLikeStatus(jwt.getSubject(),json.get("post_id"));
                Object Like_status = response.get("like_status");
                if(Like_status != null) {
                    rabbitMQ.send("ReturnLikeStatus",response.toJSONString(),rabbitMQ.setupProperties(correlationID,contentType,200,""));
                } else {
                    rabbitMQ.send("ReturnLikeStatus","",rabbitMQ.setupProperties(correlationID,contentType,(Integer) response.get("Status"),(String) response.get("Message")));
                    System.out.println(response.get("Status"));
                    System.out.println(response.get("Status") == "503");
                    System.out.println( ((Integer) response.get("Status")) == 503);
                    if(((Integer) response.get("Status")) == 503){
                        System.exit(-1);
                    }
                }
            });
            rabbitMQ.setupReceiver("Likes");
            System.out.println("The Likes service is now fully up and running!");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void ping(String url){
        try {
            InetAddress geek = InetAddress.getByName(url);
            if(geek.isReachable(5000)) System.out.println(url + " host reached!");
        } catch (IOException e) {
            System.out.println("Sorry ! We can't reach to this host: " + url);
        }
    }
}
