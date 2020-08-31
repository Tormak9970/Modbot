package modbot.database;

import modbot.utils.ReactionRoles;

import java.util.List;
import java.util.Map;

public interface DatabaseManager {
    DatabaseManager INSTANCE = new SQLiteDataSource();

    String getPrefix(long guildId);
    void setPrefix(long guildId, String newPrefix);

    List<Long> getJoinRoles(long guildId);
    void addJoinRole(long guildId, long roleID);
    void removeJoinRole(long guildId, long roleID);

    List<ReactionRoles> getReactionRoles(long guildId);
    void addReactionRole(long guildId, ReactionRoles reactRole);
    void removeReactionRole(long guildId, ReactionRoles reactRole);

    List<String> getBannedWords(long guildId);
    void addBannedWord(long guildId, String newWord);
    void removeBannedWord(long guildId, String toRemove);
}
