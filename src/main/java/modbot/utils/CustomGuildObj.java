package modbot.utils;

import java.util.ArrayList;
import java.util.List;

public class CustomGuildObj {
    private String id;
    private String name;
    private String icon;
    private List<String> features;
    private String prefix;
    private String region;
    private List<ReactionRoles> listOfReactionRoles;
    private List<CustomCommand> listOfCCs;
    private List<Long> listOfJoinRoles;
    private List<String> listOfBannedWords;
    private int numMembers;
    private boolean modOnly;
    private boolean setPrefix;
    private boolean help;
    private boolean botInfo;
    private boolean serverInfo;
    private boolean userInfo;
    private boolean banWord;
    private boolean getBannedWords;
    private boolean removeBannedWords;
    private boolean banUser;
    private boolean kickUser;
    private boolean muteUser;
    private boolean joinRole;
    private boolean reactionRole;
    private boolean removeJoinRole;

    public CustomGuildObj(String id, String name, String icon, List<String> features, String prefix, String region, List<ReactionRoles> listOfReactionRoles,
                 List<CustomCommand> listOfCCs, List<Long> listOfJoinRoles, List<String> listOfBannedWords,
                 int numMembers, boolean modOnly, boolean setPrefix, boolean help, boolean botInfo, boolean serverInfo,
                 boolean userInfo, boolean banWord, boolean getBannedWords, boolean removeBannedWords, boolean banUser,
                 boolean kickUser, boolean muteUser, boolean joinRole, boolean reactionRole, boolean removeJoinRole) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.features = features;
        this.prefix = prefix;
        this.region = region;
        this.listOfReactionRoles = listOfReactionRoles;
        this.listOfCCs = listOfCCs;
        this.listOfJoinRoles = listOfJoinRoles;
        this.listOfBannedWords = listOfBannedWords;
        this.numMembers = numMembers;
        this.modOnly = modOnly;
        this.setPrefix = setPrefix;
        this.help = help;
        this.botInfo = botInfo;
        this.serverInfo = serverInfo;
        this.userInfo = userInfo;
        this.banWord = banWord;
        this.getBannedWords = getBannedWords;
        this.removeBannedWords = removeBannedWords;
        this.banUser = banUser;
        this.kickUser = kickUser;
        this.muteUser = muteUser;
        this.joinRole = joinRole;
        this.reactionRole = reactionRole;
        this.removeJoinRole = removeJoinRole;
    }

    public CustomGuildObj(String id, String name, String icon, List<String> features, String prefix) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.features = features;
        this.prefix = prefix;
        this.listOfReactionRoles = new ArrayList<>();
        this.listOfCCs = new ArrayList<>();
        this.listOfJoinRoles = new ArrayList<>();
        this.listOfBannedWords = new ArrayList<>();
        this.region = "";
        this.numMembers = 0;
        this.modOnly = false;
        this.setPrefix = true;
        this.help = true;
        this.botInfo = true;
        this.serverInfo = true;
        this.userInfo = true;
        this.banWord = true;
        this.getBannedWords = true;
        this.removeBannedWords = true;
        this.banUser = true;
        this.kickUser = true;
        this.muteUser = true;
        this.joinRole = true;
        this.reactionRole = true;
        this.removeJoinRole = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getFeatures() {
        return features;
    }
    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<ReactionRoles> getListOfReactionRoles() {
        return listOfReactionRoles;
    }
    public void setListOfReactionRoles(List<ReactionRoles> listOfReactionRoles) {
        this.listOfReactionRoles = listOfReactionRoles;
    }

    public List<CustomCommand> getListOfCCs() {
        return listOfCCs;
    }
    public void setListOfCCs(List<CustomCommand> listOfCCs) {
        this.listOfCCs = listOfCCs;
    }

    public List<Long> getListOfJoinRoles() {
        return listOfJoinRoles;
    }
    public void setListOfJoinRoles(List<Long> listOfJoinRoles) {
        this.listOfJoinRoles = listOfJoinRoles;
    }

    public List<String> getListOfBannedWords() {
        return listOfBannedWords;
    }
    public void setListOfBannedWords(List<String> listOfBannedWords) {
        this.listOfBannedWords = listOfBannedWords;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    public int getNumMembers() {
        return numMembers;
    }
    public void setNumMembers(int numMembers) {
        this.numMembers = numMembers;
    }

    public boolean isModOnly() {
        return modOnly;
    }
    public void setModOnly(boolean modOnly) {
        this.modOnly = modOnly;
    }

    public boolean isSetPrefix() {
        return setPrefix;
    }
    public void setSetPrefix(boolean setPrefix) {
        this.setPrefix = setPrefix;
    }

    public boolean isHelp() {
        return help;
    }
    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isBotInfo() {
        return botInfo;
    }
    public void setBotInfo(boolean botInfo) {
        this.botInfo = botInfo;
    }

    public boolean isServerInfo() {
        return serverInfo;
    }
    public void setServerInfo(boolean serverInfo) {
        this.serverInfo = serverInfo;
    }

    public boolean isUserInfo() {
        return userInfo;
    }
    public void setUserInfo(boolean userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isBanWord() {
        return banWord;
    }
    public void setBanWord(boolean banWord) {
        this.banWord = banWord;
    }

    public boolean isGetBannedWords() {
        return getBannedWords;
    }
    public void setGetBannedWords(boolean getBannedWords) {
        this.getBannedWords = getBannedWords;
    }

    public boolean isRemoveBannedWords() {
        return removeBannedWords;
    }
    public void setRemoveBannedWords(boolean removeBannedWords) {
        this.removeBannedWords = removeBannedWords;
    }

    public boolean isBanUser() {
        return banUser;
    }
    public void setBanUser(boolean banUser) {
        this.banUser = banUser;
    }

    public boolean isKickUser() {
        return kickUser;
    }
    public void setKickUser(boolean kickUser) {
        this.kickUser = kickUser;
    }

    public boolean isMuteUser() {
        return muteUser;
    }
    public void setMuteUser(boolean muteUser) {
        this.muteUser = muteUser;
    }

    public boolean isJoinRole() {
        return joinRole;
    }
    public void setJoinRole(boolean joinRole) {
        this.joinRole = joinRole;
    }

    public boolean isReactionRole() {
        return reactionRole;
    }
    public void setReactionRole(boolean reactionRole) {
        this.reactionRole = reactionRole;
    }

    public boolean isRemoveJoinRole() {
        return removeJoinRole;
    }
    public void setRemoveJoinRole(boolean removeJoinRole) {
        this.removeJoinRole = removeJoinRole;
    }

    @Override
    public String toString() {
        return "Guild{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", features=" + features +
                ", prefix='" + prefix + '\'' +
                ", region='" + region + '\'' +
                ", listOfReactionRoles=" + listOfReactionRoles +
                ", listOfCCs=" + listOfCCs +
                ", listOfJoinRoles=" + listOfJoinRoles +
                ", listOfBannedWords=" + listOfBannedWords +
                ", numMembers=" + numMembers +
                ", modOnly=" + modOnly +
                ", setPrefix=" + setPrefix +
                ", help=" + help +
                ", botInfo=" + botInfo +
                ", serverInfo=" + serverInfo +
                ", userInfo=" + userInfo +
                ", banWord=" + banWord +
                ", getBannedWords=" + getBannedWords +
                ", removeBannedWords=" + removeBannedWords +
                ", banUser=" + banUser +
                ", kickUser=" + kickUser +
                ", muteUser=" + muteUser +
                ", joinRole=" + joinRole +
                ", reactionRole=" + reactionRole +
                ", removeJoinRole=" + removeJoinRole +
                '}';
    }
}
