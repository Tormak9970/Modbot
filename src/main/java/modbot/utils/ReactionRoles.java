package modbot.utils;


public class ReactionRoles {

    private long messageID;
    private long channel;
    private Long emote;
    private long role;
    private String emoji;

    public ReactionRoles(long mID, long channel, long emote, long role){
        messageID = mID;
        this.channel = channel;
        this.emote = emote;
        this.role = role;
    }

    public ReactionRoles(long mID, long channel, String emoji, long role){
        messageID = mID;
        this.channel = channel;
        this.emoji = emoji;
        this.role = role;
    }

    public long getMessageID(){
        return messageID;
    }

    public long getChannelID(){
        return channel;
    }

    public long getEmoteID(){
        return emote;
    }

    public long getRoleID(){ return role;}

    public String getEmoji(){
        return emoji;
    }

    public boolean isEmote(){
        return emote != null;
    }
}
