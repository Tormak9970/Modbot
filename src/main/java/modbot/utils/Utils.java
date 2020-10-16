package modbot.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

public abstract class Utils{
    private static final Gson gson = new Gson();

    public static Map<Long, CustomGuildObj> fullGuilds = new HashMap<>();

    public static void getFullGuilds(){
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpUriRequest request = RequestBuilder.get()
                    .setUri("http://localhost:8090/api/v1/modbot/database/guilds")
                    .build();
            CloseableHttpResponse response = client.execute(request);
            String result;
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    List<CustomGuildObj> temp;
                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    Type objType = new TypeToken<List<CustomGuildObj>>() {}.getType();
                    temp = gson.fromJson(result, objType);
                    iStream.close();

                    for(CustomGuildObj g : temp){
                        fullGuilds.put(Long.parseLong(g.getId()), g);
                    }
                }
                System.out.println(fullGuilds);
            } finally {
                client.close();
                response.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setFullGuilds(Map<Long, CustomGuildObj> guilds){
        fullGuilds = guilds;
    }
    //example of restAction
    public static void sendPrivateMessage(Member user, String content) {
        // notice that we are not placing a semicolon (;) in the callback this time!

        user.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage(content).queue());
    }

    public static void deleteHistory(int numMsg, TextChannel channel){
        Consumer<List<Message>> callback = channel::purgeMessages;
        channel.getHistory().retrievePast(numMsg).queue(callback);
    }

    public static String getUptime(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        long uptimeInSeconds = uptime / 1000;
        long numberOfHours = uptimeInSeconds / (60 * 60);
        long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
        long numberOfSeconds = uptimeInSeconds % 60;

        return String.format("Uptime is `%s hours, %s minutes, %s seconds`",
                numberOfHours, numberOfMinutes, numberOfSeconds);
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringJoiner sj = new StringJoiner("\n");
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sj.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sj.toString();
    }

    public static List<String> parse(String toParse){
        String[] tr = toParse.split("\\s");
        return Arrays.asList(tr);
    }

    public static String stringifyList(List<Long> l){
        StringJoiner j = new StringJoiner(",");
        l.forEach(i -> j.add("" + i));
        return j.toString();
    }

}
