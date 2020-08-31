package modbot.commands.moderation.bannedWords;

import modbot.database.DatabaseManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBannedWordsCommand {
    private static Map<Long, List<String>> listOfWords = new HashMap<>();

    public static List<String> getListOfBannedWords(long guildId){
        return listOfWords.computeIfAbsent(guildId, DatabaseManager.INSTANCE::getBannedWords);
    }

    public static void getCommand(GuildMessageReceivedEvent event){

        StringBuilder stringList = new StringBuilder("");
        List<String> toConvert = getListOfBannedWords(event.getGuild().getIdLong());
        for (String s : toConvert) {
            stringList.append(s).append(", ");
        }

        stringList.delete(stringList.length() - 2, stringList.length());
        event.getChannel().sendMessage("Banned words for " + event.getGuild().getName() + " are: " + stringList).queue();
    }
}
