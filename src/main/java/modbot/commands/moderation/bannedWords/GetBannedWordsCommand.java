package modbot.commands.moderation.bannedWords;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBannedWordsCommand implements CommandInterface {
    private static Gson gson = new Gson();
    public static Map<Long, List<String>> listOfWords = new HashMap<>();

    public static List<String> getListOfBannedWords(long guildId){
        return listOfWords.computeIfAbsent(guildId, GetBannedWordsCommand::getBannedWordsRequest);
    }

    private static List<String> getBannedWordsRequest(long guildId){
        List<String> bannedWords = new ArrayList<>();
        try {
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://localhost:8090/api/v1/modbot/database/bannedwords/" + guildId);
            CloseableHttpResponse res = c.execute(request);
            String result;
            try {
                HttpEntity entity = res.getEntity();

                if (entity != null){
                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    Type listType = new TypeToken<List<String>>() {}.getType();
                    bannedWords = gson.fromJson(result, listType);
                    iStream.close();
                }
            } finally {
                c.close();
                res.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bannedWords;
    }

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        StringBuilder stringList = new StringBuilder("");
        List<String> toConvert = getListOfBannedWords(event.getGuild().getIdLong());
        for (String s : toConvert) {
            stringList.append(s).append(", ");
        }

        stringList.delete(stringList.length() - 2, stringList.length());
        event.getChannel().sendMessage("Banned words for " + event.getGuild().getName() + " are: " + stringList).queue();
    }

    @Override
    public String getName() {
        return "getBannedWords";
    }

    @Override
    public String getHelp() {
        return "returns a list of banned words\n" + "Usage: $getBannedWords";
    }
}
