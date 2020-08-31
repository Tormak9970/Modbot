package modbot.commands.moderation.bannedWords;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.database.DatabaseManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBannedWordsCommandInterface implements CommandInterface {
    private static Map<Long, List<String>> listOfWords = new HashMap<>();

    public static List<String> getListOfBannedWords(long guildId){
        return listOfWords.computeIfAbsent(guildId, DatabaseManager.INSTANCE::getBannedWords);
    }

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        StringBuilder stringList = new StringBuilder("");
        List<String> toConvert = getListOfBannedWords(event.getGuild().getIdLong());
        for (String s : toConvert) {
            stringList.append(s).append(", ");
        }

        stringList.delete(stringList.length() - 2, stringList.length());
        event.getChannel().sendMessage("Banned words for " + event.getGuild().getName() + " are: " + stringList).queue();
    }

    @Override
    public String getName() {
        return "getBannedWords";
    }

    @Override
    public String getHelp() {
        return "returns a list of banned words\n" + "Usage: $getBannedWords";
    }
}
