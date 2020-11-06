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

    public static List<String> getListOfBannedWords(long guildId){
        return Utils.fullGuilds.get(guildId).getListOfBannedWords();
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
        return "words";
    }

    @Override
    public String getHelp() {
        return "returns a list of banned words\n" + "Usage: $words";
    }
}
