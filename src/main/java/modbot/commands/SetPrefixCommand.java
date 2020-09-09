package modbot.commands;


import modbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetPrefixCommand implements CommandInterface {

    private static Map<Long, String> prefixes = new HashMap<>();
    private static String defaultPrefix = "$";

    @Override
    public void handle(CommandContext ctx) {
        Member member = ctx.getMember();
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            channel.sendMessage("You must have the Manage Server permission to use his command").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage("Missing args").queue();
            return;
        }

        GuildMessageReceivedEvent event = ctx.getEvent();
        long guildID = event.getGuild().getIdLong();

        postPrefix(guildID, args.get(0));
        channel.sendMessage("prefix has been set to `" + SetPrefixCommand.getPrefix(guildID) + "`").queue();
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getHelp() {
        return "Set the prefix of the bot\n" +
                "Usage: `$prefix [new Prefix]`";
    }

    public static String getPrefix(long guildID){
        return  prefixes.computeIfAbsent(guildID, SetPrefixCommand::getPrefixRequest);
    }

    public static String getPrefixRequest(long guildId){
        String prefix = "$";
        try {
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://localhost:8090/api/v1/modbot/database/prefix/" + guildId);
            CloseableHttpResponse res = c.execute(request);
            String result;
            try {
                HttpEntity entity = res.getEntity();

                if (entity != null){
                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    prefix = result;
                    iStream.close();
                }
            } finally {
                c.close();
                res.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prefix;
    }

    public static void postPrefix(long guildId, String newPrefix){
        prefixes.replace(guildId, newPrefix);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/prefix/" + guildId)
                    .addParameter("newp", newPrefix)
                    .build();
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse res = c.execute(request);
            c.close();
            res.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public static String getDefaultPrefix(){
        return defaultPrefix;
    }
    /*
    public static void updatePrefix(long guildId, String newPrefix) {
        prefixes.replace(guildId, newPrefix);
        DatabaseManager.INSTANCE.setPrefix(guildId, newPrefix);
    }
     */

}
