package modbot.commands.info;

import me.duncte123.botcommons.messaging.EmbedUtils;
import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class BotInfoCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        Guild guild = event.getGuild();
        User user = guild.getJDA().getSelfUser();
        String botName = Objects.requireNonNull(guild.getMember(user)).getEffectiveName();
        StringBuilder roles = new StringBuilder("");
        List<Role> roleList = guild.getMember(user).getRoles();
        String update = "Debug reactionroles, add onUserJoin add role";

        for(Role role : roleList){
            roles.append(role.getName()).append(", ");
        }
        roles.delete(roles.length() - 3, roles.length() - 1);

        String generalInfo = String.format(
                "**Name**: %s\n**Region**: %s\n**Creation Date**: August 31, 2020",
                botName,
                guild.getRegion().getName()
        );

        String helpServerUrl = "https://discord.gg/4uUjsxA";
        String memberInfo = String.format(
                "**Uptime**: %s\n**Code-Type**: Open Source(GitHub)\n**Developers**: Tormak\n**Member since**: %s\n**Roles**: " + roles + "\n**Goal in Life**: overthrow the humans\n**Next Update**: " + update ,
                Utils.getUptime(),
                guild.getMember(user).getTimeJoined().getMonth() + " " + guild.getMember(user).getTimeJoined().getDayOfMonth() + ", " + guild.getMember(user).getTimeJoined().getYear(),
                helpServerUrl
        );

        String desc = "Think mee6, but no subscriptions";
        String gitHubUrl = "https://github.com/Tormak9970/Modbot";
        String inviteUrl = "https://discord.com/api/oauth2/authorize?client_id=749635340330991687&permissions=8&scope=bot";


        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                .setTitle("Info for " + botName)
                .setThumbnail(user.getAvatarUrl())
                .addField("**General Info**", generalInfo, false)
                .addField("**Other info**", memberInfo, true)
                .addField("**Description**", desc, true)
                .addField("**GitHub**", gitHubUrl, false)
                .addField("**Want to invite this bot?**", inviteUrl, false)
                .setColor(new Color(232, 156, 14))
                .setFooter("Modbot info")
                ;

        event.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "botinfo";
    }

    @Override
    public String getHelp() {
        return "gets info on this bot\n" + "Usage: $botinfo";
    }
}
