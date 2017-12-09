package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();

        // Grab user avatar
        sb.append("<div>");
        JSONObject eventTemp = response.get(0);
        JSONObject actor = eventTemp.getJSONObject("actor");
        String avatarImgUrl = actor.getString("avatar_url");
        String login = actor.getString("login");
        sb.append(String.format("<img src=%s alt='user_avatar' width=100px style='float: left'>", avatarImgUrl));
        sb.append(String.format("<div id=usertopname>%s</div>", login));
        sb.append("</div>");

        sb.append("<div style='clear:both'>");
        sb.append("<br>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            sb.append("<div class = border>");

                    // Enter commit of event
            JSONObject payload = event.getJSONObject("payload");
            JSONArray commits = payload.getJSONArray("commits");

            HashMap<String, String> shaAndMessage = new HashMap<String, String>();

            // Iterate through commits
            for(int j = 0; j < commits.length(); j++)
            {
                JSONObject commitInst = commits.getJSONObject(j);

                // Get SHA number
                String shaNum = commitInst.getString("sha");

                // Get commit message
                String message = commitInst.getString("message");

                shaAndMessage.put(shaNum, message);
            }

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");

            // Iterate through hash
            for (Map.Entry<String, String> entry : shaAndMessage.entrySet())
            {
                // Add SHA number
                sb.append("Sha: " + entry.getKey());
                sb.append("<br />");
                // Add commit message
                sb.append("Message: " + entry.getValue());
                sb.append("<br />");
            }

            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
            sb.append("</div>");
            sb.append("<br>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
        JSONObject json = Util.queryAPI(new URL(url));
        System.out.println(json);
        JSONArray events = json.getJSONArray("root");
//        for (int i = 0; i < events.length() && i < 10; i++) {
//            eventList.add(events.getJSONObject(i));
//        }

        // Get first ten PushEvents if possible
        int count = 0;
        int index = 0;
        while (count < 10 && index < events.length())
        {
            JSONObject objIndex = events.getJSONObject(index);
            if (objIndex.getString("type").equals("PushEvent"))
            {
                eventList.add(objIndex);
                count++;
            }
            index++;
        }
        return eventList;
    }
}