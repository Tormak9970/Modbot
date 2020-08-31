package modbot.commands.roles;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.database.DatabaseManager;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinRolesCommandInterface implements CommandInterface {
    private static long roleID;
    private static Map<Long, List<Long>> listOfJoinRoles = new HashMap<>();

    public static List<Long> getListOfJoinRoles(long guildId){

        return listOfJoinRoles.computeIfAbsent(guildId, DatabaseManager.INSTANCE::getJoinRoles);
    }

    public static void addRoles(long guildId, long newID){
        DatabaseManager.INSTANCE.addJoinRole(guildId, newID);
    }

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();

        if(event.getMessage().getMentionedRoles().size() > 0){
            roleID = event.getMessage().getMentionedRoles().get(0).getIdLong();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);

            EmbedBuilder embed = EmbedUtils.defaultEmbed()
                    .setTitle("Join Roles - Summary")
                    .setColor(Color.RED)
                    .addField("**Date**:", date, false)
                    .addField("**Role**: ", "" + event.getGuild().getRoleById(roleID).getAsMention(), false)
                    .setFooter("inDev Join Roles")
                    ;
            MessageChannel channel = event.getChannel();
            listOfJoinRoles.computeIfAbsent(event.getGuild().getIdLong(), s -> new ArrayList<>()).add(roleID);
            addRoles(event.getGuild().getIdLong(), roleID);
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

