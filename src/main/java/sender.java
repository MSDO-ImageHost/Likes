import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class sender {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("User","5");
            json.put("Post_ID","JegErEtPostID");
            rabbitMQ.send("UpdateLike",json.toString());
        } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
