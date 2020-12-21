import org.json.simple.JSONObject;

import java.sql.SQLException;

public class Events {
    public static JSONObject UpdateLike(String User_ID, Object Post_ID){
        JSONObject json = new JSONObject();
        if(User_ID == null || Post_ID == null) {
            json.put("Status", 400);
            json.put("Message","Malformed request syntax; User_ID or Post_ID not defined!");
            return json;
        }
        try {
            Boolean bool = mySQL.changeLike(User_ID,Post_ID.toString());
            json.put("Like status", bool);
            return json;
        } catch (SQLException e) {
            System.out.println("An error occured in the SQL connection. Cause: \n" + e.getMessage());
            e.printStackTrace(System.err);
            json.put("Status", 503);
            json.put("Message","Failed to connect to database");
            return json;
        }
    }

    public static JSONObject RequestLikesForPost(Object Post_ID) {
        JSONObject json = new JSONObject();
        if(Post_ID == null) {
            json.put("Status", 400);
            json.put("Message","Malformed request syntax; Post_ID not defined!");
            return json;
        }
        try {
            int count = mySQL.LikesForPost((String) Post_ID);
            json.put("Like amount", count);
            return json;
        } catch (SQLException e) {
            json.put("Status", 503);
            json.put("Message","Failed to connect to database");
            return json;
        }
    }
    public static JSONObject RequestLikeStatus(String User_ID, Object Post_ID) {
        JSONObject json = new JSONObject();
        if(User_ID == null || Post_ID == null) {
            json.put("Status", 400);
            json.put("Message","Malformed request syntax; User_ID or Post_ID not defined!");
            return json;
        }
        try {
            Boolean bool = mySQL.checkLike(User_ID, (String) Post_ID);
            json.put("Like status", bool);
            return json;
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getSQLState());
            json.put("Status", 503);
            json.put("Message","Failed to connect to database");
            return json;
        }
    }
}
