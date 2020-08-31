package modbot.commands;


import modbot.database.DatabaseManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class SetPrefixCommand implements ICommand{

    private static Map<Long, String> prefixes = new HashMap<>();
    private static String defaultPrefix = "$";

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        long guildID = event.getGuild().getIdLong();
        MessageChannel channel = event.getChannel();

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
                "Usage: `!!prefix [new Prefix]`";
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
