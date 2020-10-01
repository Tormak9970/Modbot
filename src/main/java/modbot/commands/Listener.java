package modbot.commands;

import modbot.CommandManager;
import modbot.commands.moderation.bannedWords.GetBannedWordsCommand;
import modbot.commands.roles.JoinRolesCommand;
import modbot.commands.roles.ReactionRolesCommand;
import modbot.utils.ReactionRoles;
import modbot.utils.Utils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Listener extends ListenerAdapter {
    private final EventWaiter waiter;
    private final CommandManager manager = new CommandManager();

    public Listener(EventWaiter waiter){
        this.waiter = waiter;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        long id = event.getGuild().getIdLong();
        System.out.println("joined");
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/guilds")
                    .addParameter("id", "" + id)
                    .build();
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse res = c.execute(request);
            c.close();
            res.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event){
        long id = event.getGuild().getIdLong();
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/guilds")
                    .addParameter("id", "" + id)
                    .build();
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpDelete request = new HttpDelete(uri);
            CloseableHttpResponse res = c.execute(request);
            c.close();
            res.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        long guildId = event.getGuild().getIdLong();
        String prefix = SetPrefixCommand.getPrefix(guildId);
        List<String> badWords = GetBannedWordsCommand.getListOfBannedWords(event.getGuild().getIdLong());

        if (event.getAuthor().isBot()) {
            return;
        }
        if(badWords.size() > 0){
            boolean isNotAloud = false;
            for(String word:badWords){
                if(event.getMessage().getContentRaw().indexOf(" " + word + " ") > 0){
                    isNotAloud = true;
                }
            }
            if(isNotAloud){
                Utils.deleteHistory(1, event.getChannel());
                event.getChannel().sendMessage("That word is not aloud in this server").queue();
            }
        }
        String raw = event.getMessage().getContentRaw();
        if (raw.startsWith(prefix)) {
            manager.handle(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if(event.getAuthor().isBot()){
            return;
        }else{
            event.getChannel().sendMessage("Why are you messaging me?").queue();
        }

    }

    @Override
    public void onReady(@NotNull ReadyEvent event){
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
        SnowflakeCacheView<Guild> temp = event.getJDA().getGuildCache();
        List<Long> ids = new ArrayList<>();
        temp.forEach(g -> ids.add(g.getIdLong()));
        String idList = Utils.stringifyList(ids);
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost:8090")
                    .setPath("/api/v1/modbot/database/guildIds")
                    .addParameter("ids", idList)
                    .build();
            CloseableHttpClient c = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse res = c.execute(request);
            c.close();
            res.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent joinEvent) {
        if (joinEvent.getUser().isBot()){
            return;
        }
        Guild guild = joinEvent.getGuild();
        long guildID = guild.getIdLong();
        List<Long> joinRoles = JoinRolesCommand.getListOfJoinRoles(guildID);

        if(joinRoles.size() > 0){
            for (Long roleId : joinRoles) {
                guild.addRoleToMember(joinEvent.getMember(), guild.getRoleById(roleId)).queue();
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent reaction) {
        List<ReactionRoles> reactionRoles = ReactionRolesCommand.getListOfReactionRoles(reaction.getGuild().getIdLong());
        if (reaction.getUser().isBot()){
            return;
        }

        Guild guild = reaction.getGuild();
        boolean match;
        if(reactionRoles != null && reactionRoles.size() > 0){
            for (ReactionRoles reactRole : reactionRoles) {

                if(reactRole.isEmote()){
                    match = Long.parseLong(reactRole.getItemID()) == reaction.getReactionEmote().getEmote().getIdLong();
                }else{
                    match = reactRole.getItemID().equals(reaction.getReactionEmote().getEmoji());
                }
                if (reactRole.getChannelID() == reaction.getChannel().getIdLong()
                        && reactRole.getMessageID() == reaction.getMessageIdLong()
                        && match) {
                    if (reactRole.getType() == 1){
                        guild.addRoleToMember(reaction.getMember(), guild.getRoleById(reactRole.getRoleID())).queue();
                        Utils.sendPrivateMessage(reaction.getMember(), "You have been given the role " + guild.getRoleById(reactRole.getRoleID()).getName() + " in the server " + guild.getName());
                    } else if (reactRole.getType() == 2){
                        guild.removeRoleFromMember(reaction.getMember(), guild.getRoleById(reactRole.getRoleID())).queue();
                        Utils.sendPrivateMessage(reaction.getMember(), "You have lost the role " + guild.getRoleById(reactRole.getRoleID()).getName() + " in the server " + guild.getName());
                    } else if (reactRole.getType() == 3){
                        guild.addRoleToMember(reaction.getMember(), guild.getRoleById(reactRole.getRoleID())).queue();
                        Utils.sendPrivateMessage(reaction.getMember(), "You have been given the role " + guild.getRoleById(reactRole.getRoleID()).getName() + " in the server " + guild.getName());
                    }
                }
            }
        }

    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent reaction){
        List<ReactionRoles> reactionRoles = ReactionRolesCommand.getListOfReactionRoles(reaction.getGuild().getIdLong());

        Guild guild = reaction.getGuild();
        boolean match;
        if(reactionRoles != null && reactionRoles.size() > 0){
            for (ReactionRoles reactRole : reactionRoles) {

                if(reactRole.isEmote()){
                    match = Long.parseLong(reactRole.getItemID()) == reaction.getReactionEmote().getEmote().getIdLong();
                }else{
                    match = reactRole.getItemID().equals(reaction.getReactionEmote().getEmoji());
                }
                if (reactRole.getChannelID() == reaction.getChannel().getIdLong()
                        && reactRole.getMessageID() == reaction.getMessageIdLong()
                        && match) {
                    if (reactRole.getType() == 3){
                        guild.removeRoleFromMember(reaction.getMember(), guild.getRoleById(reactRole.getRoleID())).queue();
                        Utils.sendPrivateMessage(reaction.getMember(), "You have lost the role " + guild.getRoleById(reactRole.getRoleID()).getName() + " in the server " + guild.getName());
                    }
                    reaction.getChannel().retrieveMessageById(reaction.getMessageIdLong()).queue(message -> {
                        if(reactRole.isEmote()){
                            if(message.getReactionById(Long.parseLong(reactRole.getItemID())) == null){
                                ReactionRolesCommand.deleteReactionRoles(reaction.getGuild().getIdLong(), reactRole);
                            }
                        } else {
                            if (message.getReactionByUnicode(reactRole.getItemID()) == null){
                                ReactionRolesCommand.deleteReactionRoles(reaction.getGuild().getIdLong(), reactRole);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent reaction){
        List<ReactionRoles> reactionRoles = ReactionRolesCommand.getListOfReactionRoles(reaction.getGuild().getIdLong());

        if(reactionRoles != null && reactionRoles.size() > 0){
            for (ReactionRoles reactRole : reactionRoles) {

                if (reactRole.getChannelID() == reaction.getChannel().getIdLong()
                        && reactRole.getMessageID() == reaction.getMessageIdLong()) {
                    ReactionRolesCommand.deleteReactionRoles(reaction.getGuild().getIdLong(), reactRole);
                }
            }
        }
    }
}
