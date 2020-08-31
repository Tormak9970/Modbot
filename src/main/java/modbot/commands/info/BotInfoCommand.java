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
                "**Uptime**: %s\n**Code-Type**: Open Source(GitHub)\n**Developers**: Tormak9970\n**Member since**: %s\n**Roles**: " + roles + "\n**Goal in Life**: overthrow the humans\n**Next Update**: " + update ,
                Utils.getUptime(),
                guild.getMember(user).getTimeJoined().getMonth() + " " + guild.getMember(user).getTimeJoined().getDayOfMonth() + ", " + guild.getMember(user).getTimeJoined().getYear(),
                helpServerUrl
        );

        String desc = "inDev Bot is a discord bot that I started " +
                "working on during the Corona virus pandemic. " +
                "It can do a multitude of tasks including: " +
                "Moderation, Reaction Roles, add prefixes to a " +
                "username, play music, play rocket league mafia, " +
                "and of course, send memes from r/dankmemes.";
        String gitHubUrl = "https://github.com/Tormak9970/DiscordBot";
        String inviteUrl = "https://discordapp.com/api/oauth2/authorize?client_id=643451410855362569&permissions=8&scope=bot";


        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                .setTitle("Info for " + botName)
                .setThumbnail(user.getAvatarUrl())
                .addField("**General Info**", generalInfo, false)
                .addField("**Other info**", memberInfo, true)
                .addField("**Description**", desc, true)
                .addField("**GitHub**", gitHubUrl, false)
                .addField("**Want to invite this bot?**", inviteUrl, false)
                .setColor(new Color(179, 21, 214))
                .setFooter("inDev info")
                ;

        event.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }
}
