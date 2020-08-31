package modbot.commands.moderation.bannedWords;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.database.DatabaseManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class RemoveBannedWord implements CommandInterface {

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
        DatabaseManager.INSTANCE.removeBannedWord(event.getGuild().getIdLong(), ctx.getArgs().get(0));
    }

    @Override
    public String getName() {
        return "removebannedword";
    }

    @Override
    public String getHelp() {
        return "removes the specified word from the banned words list\n" + "Usage: $removeBannedWord [word]";
    }
}
