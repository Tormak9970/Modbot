package modbot.commands.customCommands;

public enum CCVariables {
    USER("{user}"),
    MENTION_ROLE("{role}"),
    LINK_CHANNEL("{channel}"),
    MENTION_EVERYONE("{everyone}"),
    MENTION_HERE("{here}"),

    USER_ID("{user.id}"),
    USER_NAME("{user.name}"),
    USER_USERNAME("{user.username}"),
    USER_DISCRM("{user.discriminator}"),
    USER_NICK("{user.nick}"),
    USER_GAME("{user.game}"),
    USER_AVATAR("{user.avatar}"),
    USER_MENTION("{user.mention}"),
    USER_ACC_CREATION_DATE("{user.createdAt}"),
    USER_SERVER_JOINED_DATE("{user.joinedAt}"),

    SERVER_ID("{server.id}"),
    SERVER_NAME("{server.name}"),
    SERVER_ICON("{server.icon}"),
    SERVER_MEMBER_COUNT("{server.memberCount}"),
    SERVER_OWNER_ID("{server.ownerID}"),
    SERVER_CREATION_DATE("{server.createdAt}"),
    SERVER_REGION("{server.region}"),

    CHANNEL_ID("{channel.id}"),
    CHANNEL_NAME("{channel.name}"),
    CHANNEL_MENTION("{channel.mention}"),

    TIME_24("{time}"),
    TIME_12("{time12}"),
    DATE("{date}"),
    DATE_AND_TIME_24("{datetime}"),
    DATE_AND_TIME_12("{datetime12}"),

    OUTPUT_COMMAND_PREFIX("{prefix}"),
    DELETE_COMMAND_MSG("{delete}"),
    DISABLE_BOT_RESPONSE("{silent}"),
    INPUT_PARAM("$N"),
    //can be more then 1
    SET_REQUIRED_ROLE("{require-role}:"),
    //no more then 1
    SET_REQUIRED_CHANNEL("{require-channel}:"),
    BLACKLIST_ROLE("{not-role}:"),
    BLACKLIST_CHANNEL("{not-channel}:"),
    /*COMMAND("{command}:"),*/
    SEND_RESPONSE_IN_DM("{dm}"),
    SEND_RESPONSE_IN_CHANNEL("{respond}:"),
    LIST_CHOICES("{choose}:"),
    GET_CHOICE("{choice}");

    private String optionText;
    CCVariables(String optionText){
        this.optionText = optionText;
    }

    @Override
    public String toString() {
        return optionText;
    }
}
