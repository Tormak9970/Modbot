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
        final TextChannel channel = ctx.getChannel();
        final Message message = ctx.getMessage();
        final Member member = ctx.getMember();
        final List<String> args = ctx.getArgs();

        if (args.size() < 2 || message.getMentionedMembers().isEmpty()) {
            channel.sendMessage("Missing arguments").queue();
            return;
        }

        final Member target = message.getMentionedMembers().get(0);

        if (member.getIdLong() == target.getIdLong()) {
            channel.sendMessage("You gotta be pretty stupid to try and kick yourself").queue();
            return;
        }

        if (!member.canInteract(target) || !member.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("You are missing permission to kick this member").queue();
            return;
        }

        final Member selfMember = ctx.getSelfMember();

        if (!selfMember.canInteract(target) || !selfMember.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("I am missing permissions to kick that member").queue();
            return;
        }

        final String reason = String.join(" ", args.subList(1, args.size()));

        ctx.getGuild()
                .kick(target, reason)
                .reason(reason)
                .queue(
                        (__) -> channel.sendMessage("Kick was successful").queue(),
                        (error) -> channel.sendMessageFormat("Could not kick %s", error.getMessage()).queue()
                );
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
                    channel.sendMessage(toKick.getEffectiveName() + " has been muted for " + reason).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
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
