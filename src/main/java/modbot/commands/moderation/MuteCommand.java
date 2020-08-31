package modbot.commands.moderation;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MuteCommand implements CommandInterface {
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
        Message message = event.getMessage();
        String content = message.getContentRaw();
        int muteLength = 0;
        int numArgs = args.size();
        long memberID = 0;

        if(numArgs == 1){
            muteLength = 5;
            memberID = event.getGuild().getMembersByEffectiveName(args.get(0), true).get(0).getIdLong();
        }else if(numArgs == 2){
            String stringTime = args.get(1);
            memberID = event.getGuild().getMembersByEffectiveName(content.substring(content.indexOf(" "), content.lastIndexOf(" ")), true).get(0).getIdLong();
            try{
                muteLength = Integer.parseInt(stringTime);
            }catch(NumberFormatException e){
                event.getChannel().sendMessage("Not a valid length").queue();
                return;
            }
        } else {
            event.getChannel().sendMessage("Error. you probably are missing an argument").queue();
            return;
        }

        Member toMute = event.getGuild().getMemberById(memberID);

        toMute.mute(true).queue();

        channel.sendMessage(toMute.getEffectiveName() + " has been muted for " + muteLength + " minutes").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)

        toMute.mute(false).queueAfter(muteLength, TimeUnit.MINUTES);
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getHelp() {
        return "mutes the specified user for the specified amount of time\n" + "Usage: $mute [] []";
    }
}
