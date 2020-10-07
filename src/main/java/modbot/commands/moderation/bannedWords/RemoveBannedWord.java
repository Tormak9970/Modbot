package modbot.commands.moderation.bannedWords;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class RemoveBannedWord implements CommandInterface {

    private static void deleteBannedWord(long guildId, String word){
        Utils.fullGuilds.get(guildId).removeBannedWord(word);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/bannedwords/" + guildId)
                    .addParameter("word", word)
                    .build();
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpDelete request = new HttpDelete(uri);
            CloseableHttpResponse response = client.execute(request);
            client.close();
            response.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

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
        RemoveBannedWord.deleteBannedWord(event.getGuild().getIdLong(), ctx.getArgs().get(0));
    }

    @Override
    public String getName() {
        return "removebannedword";
    }

    @Override
    public String getHelp() {
        return "removes the specified word from the banned words list\n" + "Usage: $removeBannedWord [word]";
    }
}
