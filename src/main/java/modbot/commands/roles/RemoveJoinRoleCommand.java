package modbot.commands.roles;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RemoveJoinRoleCommand implements CommandInterface {


    private static void deleteJoinRoles(long guildId, long roleId){
        Utils.fullGuilds.get(guildId).removeJoinRole(roleId);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/joinroles/" + guildId)
                    .addParameter("roleid", "" + roleId)
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
        GuildMessageReceivedEvent event = ctx.getEvent();
        if (!ctx.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            ctx.getChannel().sendMessage("You must have the Manage Server permission to use his command").queue();
            return;
        }
        if (Utils.fullGuilds.get(ctx.getGuild().getIdLong()).getListOfJoinRoles().indexOf(ctx.getGuild().getIdLong()) > 0){
            if (event.getMessage().getMentionedRoles().size() > 0){
                deleteJoinRoles(ctx.getGuild().getIdLong(), event.getMessage().getMentionedRoles().get(0).getIdLong());
            } else {
                event.getChannel().sendMessage("Please mention a role as part of the command.").queue();
            }
        } else {
            event.getChannel().sendMessage("You have no join roles setup.").queue();
        }
    }

    @Override
    public String getName() {
        return "removejoinrole";
    }

    @Override
    public String getHelp() {
        return "removes a previously setup joinrole\n" + "Usage: $removejoinrole [@mention role]";
    }
}
