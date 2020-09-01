package modbot.commands.moderation;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.List;

public class BanCommand implements CommandInterface {
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
            channel.sendMessage("You gotta be pretty stupid to try and ban yourself").queue();
            return;
        }

        if (!member.canInteract(target) || !member.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("You are missing permission to ban this member").queue();
            return;
        }

        final Member selfMember = ctx.getSelfMember();

        if (!selfMember.canInteract(target) || !selfMember.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("I am missing permissions to ban that member").queue();
            return;
        }

        final String reason = String.join(" ", args.subList(1, args.size()));

        ctx.getGuild()
                .ban(target, 30)
                .reason(reason)
                .queue(
                        (__) -> channel.sendMessage("Ban was successful").queue(),
                        (error) -> channel.sendMessageFormat("Could not ban %s", error.getMessage()).queue()
                );
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getHelp() {
        return "bans member and deletes messages from *X* days ago\n" + "Usage: $ban [@mention] [reason]";
    }
}
