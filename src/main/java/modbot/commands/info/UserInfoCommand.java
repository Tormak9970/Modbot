package modbot.commands.info;

import me.duncte123.botcommons.messaging.EmbedUtils;
import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class UserInfoCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        List<String> args = ctx.getArgs();
        Member memInfo = event.getGuild().getMembersByEffectiveName(args.get(0), true).get(0);
        User toGetInfo = memInfo.getUser();
        StringBuilder roles = new StringBuilder("");
        String nickname = "";

        try{
            nickname = memInfo.getNickname();
        }catch(NullPointerException ignored){

        }

        for (int i = 0; i < memInfo.getRoles().size(); i++){
            Role role = memInfo.getRoles().get(i);
            if(role.isMentionable()){
                roles.append(role.getAsMention());
            }else{
                roles.append(role.getName());
            }
            if(i == memInfo.getRoles().size()){

            }else{
                roles.append(", ");
            }
        }

        String generalInfo = String.format(
                "**Name**: %s\n**Account created on**: %s",
                toGetInfo.getName(),
                toGetInfo.getTimeCreated().getMonth() + " " + toGetInfo.getTimeCreated().getDayOfMonth() + ", " + toGetInfo.getTimeCreated().getYear()
        );

        String memberInfo = String.format(
                "**Nickname**: %s\n**Joined on**: %s\n**Online**: %s\n**Roles**: %s",
                nickname,
                memInfo.getTimeJoined().getMonth() + " " + memInfo.getTimeJoined().getDayOfMonth() + ", " + memInfo.getTimeJoined().getYear(),
                memInfo.getOnlineStatus(),
                roles
        );

        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                .setTitle("User info on " + toGetInfo.getName())
                .setThumbnail(toGetInfo.getAvatarUrl())
                .addField("General Info", generalInfo, false)
                .addField("Server Related", memberInfo, false)
                .setFooter("inDev userInfo")
                ;

        event.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public String getHelp() {
        return "gives info on specified user\n" + "Usage: $userinfo [user]";
    }
}
