import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class sender {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("User","5");
            json.put("Post_ID","JegErEtPostID");
            rabbitMQ.send("RequestLikeStatus",json.toString());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
