package modbot.commands.moderation;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KickCommand implements CommandInterface {

    private static EventWaiter waiter;
    private static long guildID;
    private static long setup;

    @Override
    public void handle(CommandContext ctx) {

        Member member = ctx.getMember();
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        GuildMessageReceivedEvent event = ctx.getEvent();

        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            channel.sendMessage("You must have the Manage Server permission to use his command").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage("Missing args").queue();
            return;
        }

        waiter = ctx.getWaiter();
        guildID = event.getGuild().getIdLong();
        setup = event.getChannel().getIdLong();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        String reason = "";
        int numSpaces = args.size();
        long memberID = 0;

        memberID = event.getGuild().getMembersByEffectiveName(args.get(0), true).get(0).getIdLong();

        if(numSpaces == 1){
            event.getChannel().sendMessage("Please provide a reason").queue();
        }else if(event.getAuthor().getIdLong() == memberID){
            event.getChannel().sendMessage("You have got to be pretty stupid to try to kick yourself").queue();
        }else if(numSpaces == 2){
            reason = args.get(1);
            event.getChannel().sendMessage("Are you sure you want to kick this person?").queue();
            initWaiter(event.getJDA().getShardManager(), reason, memberID);
        }
    }

    private static void initWaiter(ShardManager shardManager, String reason, long memberID){
        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                (event) -> {
                    User user = event.getAuthor();
                    boolean isYes = event.getMessage().getContentRaw().equals("yes");

                    return !user.isBot() && isYes && event.getChannel().getIdLong() == setup && event.getGuild().getIdLong() == guildID;
                },
                (event) -> {
                    Member toKick = event.getGuild().getMemberById(memberID);

                    toKick.kick(reason).queue();

                    MessageChannel channel = event.getChannel();
                    //channel.sendMessage(toKick.getEffectiveName() + " has been muted for " + reason + " minutes").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
                },
                30, TimeUnit.SECONDS,
                () -> {
                    TextChannel textChannel = shardManager.getTextChannelById(setup);
                    textChannel.sendMessage("Your response has timed out due to un responsiveness. please restart.").queue();
                }
        );
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getHelp() {
        return "kicks specified user from server\n" + "Usage: $kick [user]";
    }
}
