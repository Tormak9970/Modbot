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

public class BanCommand implements CommandInterface {
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
        String reason = "";
        int numSpaces = args.size();
        long memberID = 0;

        memberID = event.getGuild().getMembersByEffectiveName(args.get(0), true).get(0).getIdLong();

        if(numSpaces == 1){
            event.getChannel().sendMessage("You are missing 2 args").queue();
        }else if(event.getAuthor().getIdLong() == memberID){
            event.getChannel().sendMessage("You have got to be pretty stupid to try to ban yourself").queue();
        }else if(numSpaces == 2){
            event.getChannel().sendMessage("You are missing 1 args").queue();
        }else if(numSpaces == 3){
            int delDays = Integer.parseInt(args.get(1), content.lastIndexOf(" "));
            reason = args.get(2);
            event.getChannel().sendMessage("Are you sure you want to ban this person?").queue();
            initWaiter(event.getJDA().getShardManager(), reason, memberID, delDays);
        }
    }

    private static void initWaiter(ShardManager shardManager, String reason, long memberID, int delDays){
        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                (event) -> {
                    User user = event.getAuthor();
                    boolean isYes = event.getMessage().getContentRaw().equals("yes");

                    return !user.isBot() && isYes && event.getChannel().getIdLong() == setup && event.getGuild().getIdLong() == guildID;
                },
                (event) -> {
                    Member toBan = event.getGuild().getMemberById(memberID);

                    toBan.ban(delDays, reason).queue();

                    MessageChannel channel = event.getChannel();
                    channel.sendMessage(toBan.getEffectiveName() + " has been banned for " + reason).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
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
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }
}
