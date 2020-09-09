package modbot.commands.roles;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import me.duncte123.botcommons.messaging.EmbedUtils;
import modbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinRolesCommand implements CommandInterface {
    private static Gson gson = new Gson();
    public static Map<Long, List<Long>> listOfJoinRoles = new HashMap<>();

    public static List<Long> getListOfJoinRoles(long guildId){
        return listOfJoinRoles.computeIfAbsent(guildId, JoinRolesCommand::getJoinRolesRequest);
    }

    private static List<Long> getJoinRolesRequest(long guildId){
        List<Long> roleIds = new ArrayList<>();
        try {
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://localhost:8090/api/v1/modbot/database/joinroles/" + guildId);
            CloseableHttpResponse res = c.execute(request);
            String result;
            try {
                HttpEntity entity = res.getEntity();

                if (entity != null){
                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    Type listType = new TypeToken<List<Long>>() {}.getType();
                    roleIds = gson.fromJson(result, listType);
                    iStream.close();
                }
            } finally {
                c.close();
                res.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return roleIds;
    }

    private static void postJoinRoles(long guildId, long roleId){
        listOfJoinRoles.computeIfAbsent(guildId, s -> new ArrayList<>()).add(roleId);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/joinroles/" + guildId)
                    .addParameter("roleid", "" + roleId)
                    .build();
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse response = client.execute(request);
            client.close();
            response.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();

        if(event.getMessage().getMentionedRoles().size() > 0){
            long roleID;
            roleID = event.getMessage().getMentionedRoles().get(0).getIdLong();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);

            EmbedBuilder embed = EmbedUtils.defaultEmbed()
                    .setTitle("Join Roles - Summary")
                    .setColor(new Color(232, 156, 14))
                    .addField("**Date**:", date, false)
                    .addField("**Role**: ", "" + event.getGuild().getRoleById(roleID).getAsMention(), false)
                    .setFooter("Modbot Join Roles")
                    ;
            MessageChannel channel = event.getChannel();
            postJoinRoles(event.getGuild().getIdLong(), roleID);
            channel.sendMessage(embed.build()).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }else{
            event.getChannel().sendMessage("Please mention a role in your command call.").queue();
        }
    }

    @Override
    public String getName() {
        return "joinrole";
    }

    @Override
    public String getHelp() {
        return "adds a role given to users upon joining the server\n" + "Usage: $joinrole [@mention role]";
    }
}

