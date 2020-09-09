package modbot.utils;


public class ReactionRoles {

    private long messageID;
    private long channelID;
    private long roleID;
    private String itemID;
    private boolean isemote;
    private int type;

    public ReactionRoles(long messageID, long channelID, String itemID, long roleID, boolean isemote, int type){
        this.messageID = messageID;
        this.channelID = channelID;
        this.itemID = itemID;
        this.roleID = roleID;
        this.isemote = isemote;
        this.type = type;
    }

    public long getMessageID(){
        return messageID;
    }

    public long getChannelID(){
        return channelID;
    }

    public long getRoleID(){ return roleID;}

    public String getItemID(){
        return itemID;
    }

    public boolean isEmote(){
        return isemote;
    }

    public int getType() {
        return type;
    }
}
