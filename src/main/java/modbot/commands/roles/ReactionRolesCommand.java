package modbot.commands.roles;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.messaging.EmbedUtils;
import modbot.commands.SetPrefixCommand;
import modbot.utils.ReactionRoles;
import modbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static modbot.utils.Utils.deleteHistory;

//Bugged, needs to be fixed

public class ReactionRolesCommand extends ListenerAdapter {
    private static final Gson gson = new Gson();
    private EventWaiter eventWaiter;
    private long setup;
    private static Map<Long, List<ReactionRoles>> listOfReactionRoles = new HashMap<>();
    private int choice;
    private String itemID = "";
    private long guildID;
    private long roleID;
    private long msgChannelID;
    private long messageID;

    public ReactionRolesCommand(EventWaiter waiter){
        eventWaiter = waiter;
    }

    public static List<ReactionRoles> getListOfReactionRoles(long guildId){
        return listOfReactionRoles.computeIfAbsent(guildId, ReactionRolesCommand::getReactionRoles);
    }
    private static List<ReactionRoles> getReactionRoles(long guildId){
        List<ReactionRoles> rr = new ArrayList<>();
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://localhost:8090/api/v1/modbot/database/reactionroles/" + guildId);
            CloseableHttpResponse response = client.execute(request);
            String result;
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    Type listType = new TypeToken<List<ReactionRoles>>() {}.getType();
                    rr = gson.fromJson(result, listType);
                    iStream.close();
                }
            } finally {
                client.close();
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rr;
    }

    private static void postReactionRoles(long guildId, ReactionRoles rr){
        listOfReactionRoles.computeIfAbsent(guildId, s -> new ArrayList<>()).add(rr);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/reactionroles/" + guildId)
                    .addParameter("messageid", "" + rr.getMessageID())
                    .addParameter("channelid", "" + rr.getChannelID())
                    .addParameter("isemote", "" + rr.isEmote())
                    .addParameter("roleid", "" + rr.getRoleID())
                    .addParameter("itemid", rr.getItemID())
                    .addParameter("isemote", "" + rr.isEmote())
                    .addParameter("type", "" + rr.getType())
                    .build();
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse response = client.execute(request);
            client.close();
            response.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void deleteReactionRoles(long guildId, ReactionRoles rr){
        listOfReactionRoles.get(guildId).remove(rr);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/reactionroles/" + guildId)
                    .addParameter("messageid", "" + rr.getMessageID())
                    .addParameter("channelid", "" + rr.getChannelID())
                    .addParameter("isemote", "" + rr.isEmote())
                    .addParameter("roleid", "" + rr.getRoleID())
                    .addParameter("itemid", rr.getItemID())
                    .addParameter("isemote", "" + rr.isEmote())
                    .addParameter("type", "" + rr.getType())
                    .build();
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpDelete request = new HttpDelete(uri);
            CloseableHttpResponse response = client.execute(request);
            client.close();
            response.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //works
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e)
    {
        if (e.getAuthor().isBot()) return;
        Message message = e.getMessage();
        String content = message.getContentRaw();
        if (content.equals(SetPrefixCommand.getPrefix(e.getGuild().getIdLong()) + "rr"))
        {
            TextChannel channel = e.getChannel();
            Guild guild = e.getGuild();
            User u = guild.getJDA().getSelfUser();
            setup = e.getChannel().getIdLong();
            guildID = guild.getIdLong();

            EmbedBuilder embed = EmbedUtils.defaultEmbed()
                    .setTitle("Reaction Roles")
                    .setColor(Color.RED)
                    .setThumbnail(u.getAvatarUrl())
                    .addField("**Step 1**: ", "please mention channel " +
                            "\nthat the reaction role will be in." +
                            "\n(you need to enable developer mode)" +
                            "\nSettings -> Appearance -> Advanced", false)
                    .setColor(new Color(232, 156, 14))
                    .setFooter("Modbot Reaction Roles")
                    ;
            channel.sendMessage(embed.build()).queue();

            ShardManager shardManager = e.getJDA().getShardManager();
            eventWaiter.waitForEvent(
                    GuildMessageReceivedEvent.class,
                    (event) -> {
                        User user = event.getAuthor();
                        boolean channelMentioned = event.getMessage().getMentionedChannels().size() != 0;

                        return !user.isBot() && channelMentioned && event.getChannel().getIdLong() == setup && event.getGuild().getIdLong() == guildID;
                    },
                    (event) -> getRRChannelID(event, shardManager),
                    30, TimeUnit.SECONDS,
                    () -> {
                        TextChannel textChannel = shardManager.getTextChannelById(setup);
                        textChannel.sendMessage("Your reaction role has timed out due to un responsiveness. please restart.").queue();
                    }
            );
        }
    }

    private void getRRChannelID(GuildMessageReceivedEvent event, ShardManager shardManager){
        System.out.println("ran second");
        TextChannel textChannel = shardManager.getTextChannelById(setup);
        Guild guild = event.getGuild();
        User botUser = guild.getJDA().getSelfUser();

        //code to execute
        msgChannelID = event.getMessage().getMentionedChannels().get(0).getIdLong();
        deleteHistory(2, event.getGuild().getTextChannelById(msgChannelID));

        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                .setTitle("Reaction Roles")
                .setColor(Color.RED)
                .setThumbnail(botUser.getAvatarUrl())
                .addField("**Step 2**: ", "please send the message id " +
                        "\nthat the reaction role will be on.", false)
                .setColor(new Color(232, 156, 14))
                .setFooter("Modbot Reaction Roles")
                ;
        textChannel.sendMessage(embed.build()).queue();


        //new event waiter
        eventWaiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                (event1) -> {
                    System.out.println("ran second check");
                    User user = event1.getAuthor();
                    if (!user.isBot() && event1.getChannel().getIdLong() == setup && event1.getGuild().getIdLong() == guildID) {
                        boolean isId = checkMsgId(event1.getGuild(), event1.getMessage().getContentRaw());
                        return isId;
                    }
                    return false;
                },
                (event1) -> getRRMessageID(event1, shardManager, botUser),
                30, TimeUnit.SECONDS,
                () -> {
                    System.out.println("ran second failed");
                    TextChannel textChannel1 = shardManager.getTextChannelById(setup);
                    textChannel1.sendMessage("Your reaction role has timed out due to un responsiveness. please restart.").queue();
                }
        );
    }

    private void getRRMessageID(GuildMessageReceivedEvent event, ShardManager shardManager, User botUser){
        System.out.println("ran third");
        messageID = Long.parseLong(event.getMessage().getContentRaw());

        deleteHistory(2, event.getGuild().getTextChannelById(msgChannelID));

        EmbedBuilder embed2 = EmbedUtils.defaultEmbed()
                .setTitle("Reaction Roles")
                .setThumbnail(botUser.getAvatarUrl())
                .addField("**Step 3**: ", "please @mention the role " +
                        "\nthat will be given, you may have to enable pinging of" +
                        "\nit, but u can turn it off later.", false)
                .setColor(new Color(232, 156, 14))
                .setFooter("Modbot Reaction Roles")
                ;
        event.getChannel().sendMessage(embed2.build()).queue();

        eventWaiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                (event1) -> {
                    System.out.println("ran third check");
                    User user = event1.getAuthor();
                    boolean hasRole = event1.getMessage().getMentionedRoles().size() != 0;
                    return !user.isBot() && event1.getChannel().getIdLong() == setup && event1.getGuild().getIdLong() == guildID && hasRole;
                },
                (event1) -> getRRRoleID(event1, shardManager, botUser),
                30, TimeUnit.SECONDS,
                () -> {
                    System.out.println("ran third failed");
                    TextChannel textChannel1 = shardManager.getTextChannelById(setup);
                    textChannel1.sendMessage("Your reaction role has timed out due to un responsiveness. please restart.").queue();
                }
        );

    }

    private void getRRRoleID(GuildMessageReceivedEvent event, ShardManager shardManager, User botUser){
        System.out.println("ran fourth");
        roleID = event.getMessage().getMentionedRoles().get(0).getIdLong();

        deleteHistory(2, event.getGuild().getTextChannelById(msgChannelID));

        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                .setTitle("Reaction Roles")
                .setThumbnail(botUser.getAvatarUrl())
                .addField("**Step 4**: ", "Choose one:" +
                        "\n`1:` adding reaction only gives role" +
                        "\n`2:` adding reaction only removes role" +
                        "\n`3:` adding/removing reaction adds/removes role", false)
                .setColor(new Color(232, 156, 14))
                .setFooter("Modbot Reaction Roles")
                ;
        event.getChannel().sendMessage(embed.build()).queue();

        eventWaiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                (event1) -> {
                    System.out.println("ran fourth check");
                    User user = event1.getAuthor();
                    boolean isChoice = false;
                    try{
                        int testChoice = Integer.parseInt(event1.getMessage().getContentRaw());
                        if (1 <= testChoice && testChoice <= 3){
                            isChoice = true;
                        }
                    }catch (NumberFormatException ignored){

                    }

                    return !user.isBot() && event1.getChannel().getIdLong() == setup && event1.getGuild().getIdLong() == guildID && isChoice;
                },
                (event1) -> getRRType(event1, shardManager, botUser),
                30, TimeUnit.SECONDS,
                () -> {
                    System.out.println("ran fourth failed");
                    TextChannel textChannel1 = shardManager.getTextChannelById(setup);
                    assert textChannel1 != null;
                    textChannel1.sendMessage("Your reaction role has timed out due to un responsiveness. please restart.").queue();
                }
        );
    }

    private void getRRType(GuildMessageReceivedEvent event, ShardManager shardManager, User botUser){
        System.out.println("ran fifth");
        choice = Integer.parseInt(event.getMessage().getContentRaw());

        deleteHistory(2, event.getGuild().getTextChannelById(msgChannelID));
        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                .setTitle("Reaction Roles")
                .setThumbnail(botUser.getAvatarUrl())
                .addField("**Step 5**: ", "Please react to" +
                        "\nthis message with your desired emote.", false)
                .setColor(new Color(232, 156, 14))
                .setFooter("Modbot Reaction Roles")
                ;
        event.getChannel().sendMessage(embed.build()).queue(
                (message) -> {
                    long embedID = message.getIdLong();
                    eventWaiter.waitForEvent(
                            GuildMessageReactionAddEvent.class,
                            (event1) -> {
                                System.out.println("ran fifth check");
                                User user = event1.getUser();
                                boolean isEmbed = embedID == event1.getMessageIdLong();
                                return !user.isBot() && event1.getChannel().getIdLong() == setup && event1.getGuild().getIdLong() == guildID && isEmbed;
                            },
                            (event1) -> getRREmoteID(event1),
                            30, TimeUnit.SECONDS,
                            () -> {
                                System.out.println("ran fifth failed");
                                TextChannel textChannel1 = shardManager.getTextChannelById(setup);
                                assert textChannel1 != null;
                                textChannel1.sendMessage("Your reaction role has timed out due to un responsiveness. please restart.").queue();
                            }

                    );
                }
        );
    }

    //works
    private void getRREmoteID(GuildMessageReactionAddEvent event){
        Guild guild = event.getGuild();
        boolean isEmoji = event.getReactionEmote().isEmoji();
        if(isEmoji){
            itemID = event.getReactionEmote().getEmoji();
            deleteHistory(2, event.getGuild().getTextChannelById(msgChannelID));
            guild.getTextChannelById(msgChannelID).retrieveMessageById(messageID).queue(
                    (message) -> {
                        message.addReaction(itemID).queue();
                        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                                .setTitle("Reaction Roles - Summary")
                                .addField("**Reaction ID**: ", itemID, true)
                                .addField("**Emoji**: ", "" + itemID, true)
                                .addField("**Type**: ", "" + choice, true)
                                .addField("**Message ID**: ", "" + messageID, false)
                                .addField("**Channel**: ", "" + event.getGuild().getTextChannelById(msgChannelID).getAsMention(), true)
                                .addField("**Role**: ", "" + event.getGuild().getRoleById(roleID).getAsMention(), true)
                                .setColor(new Color(232, 156, 14))
                                .setFooter("Modbot Reaction Roles")
                                ;
                        guild.getTextChannelById(setup).sendMessage(embed.build()).queue();
                    }
            );
        } else {
            itemID = String.valueOf(event.getReactionEmote().getEmote().getIdLong());
            deleteHistory(2, event.getGuild().getTextChannelById(msgChannelID));
            guild.getTextChannelById(msgChannelID).retrieveMessageById(messageID).queue(
                    (message) -> {
                        message.addReaction(guild.getEmoteById(Long.parseLong(itemID))).queue();
                        EmbedBuilder embed = EmbedUtils.defaultEmbed()
                                .setTitle("Reaction Roles - Summary")
                                .addField("**Reaction ID**: ", itemID, true)
                                .addField("**Emoji**: ", "" + event.getGuild().getEmoteById(Long.parseLong(itemID)), true)
                                .addField("**Type**: ", "" + choice, true)
                                .addField("**Message ID**: ", "" + messageID, false)
                                .addField("**Channel**: ", "" + event.getGuild().getTextChannelById(msgChannelID).getAsMention(), true)
                                .addField("**Role**: ", "" + event.getGuild().getRoleById(roleID).getAsMention(), true)
                                .setColor(new Color(232, 156, 14))
                                .setFooter("Modbot Reaction Roles")
                                ;
                        guild.getTextChannelById(setup).sendMessage(embed.build()).queue();
                    }
            );
        }
        ReactionRoles reactRole = new ReactionRoles(messageID, msgChannelID, itemID, roleID, !isEmoji, choice);
        postReactionRoles(guildID, reactRole);

    }

    private boolean checkMsgId(Guild guild, String mid) {
        long id = Long.parseLong(mid);
        RestAction action = guild
                .getTextChannelById(msgChannelID)
                .retrieveMessageById(id);

        try {
            return action.complete() != null;
        } catch (ErrorResponseException e) {
            if (e.getErrorCode() == 404) {
                return false;
            }
            throw e;
        }
    }
}

