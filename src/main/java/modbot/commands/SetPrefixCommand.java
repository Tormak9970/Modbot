package modbot.commands;


import modbot.database.DatabaseManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetPrefixCommand implements CommandInterface {

    private static Map<Long, String> prefixes = new HashMap<>();
    private static String defaultPrefix = "$";

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
        long guildID = event.getGuild().getIdLong();

        updatePrefix(guildID, event.getMessage().getContentRaw().substring(SetPrefixCommand.getPrefix(guildID).length() + 10));
        channel.sendMessage("prefix has been set to `" + SetPrefixCommand.getPrefix(guildID) + "`").queue();
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getHelp() {
        return "Set the prefix of the bot\n" +
                "Usage: `$prefix [new Prefix]`";
    }

    public static String getPrefix(long guildID){
        return  prefixes.computeIfAbsent(guildID, DatabaseManager.INSTANCE::getPrefix);
    }

    public static String getDefaultPrefix(){
        return defaultPrefix;
    }

    public static void updatePrefix(long guildId, String newPrefix) {
        prefixes.replace(guildId, newPrefix);
        DatabaseManager.INSTANCE.setPrefix(guildId, newPrefix);
    }

}
