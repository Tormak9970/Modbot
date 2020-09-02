package modbot.commands;

import modbot.CommandManager;
import modbot.commands.moderation.bannedWords.GetBannedWordsCommandInterface;
import modbot.commands.roles.JoinRolesCommandInterface;
import modbot.commands.roles.ReactionRolesCommand;
import modbot.utils.ReactionRoles;
import modbot.utils.Utils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class Listener extends ListenerAdapter {
    private final EventWaiter waiter;
    private final CommandManager manager = new CommandManager();

    public Listener(EventWaiter waiter){
        this.waiter = waiter;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String prefix = SetPrefixCommand.getPrefix(event.getGuild().getIdLong());
        List<String> badWords = GetBannedWordsCommandInterface.getListOfBannedWords(event.getGuild().getIdLong());

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

    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent joinEvent) {
        if (joinEvent.getUser().isBot()){
            return;
        }
        Guild guild = joinEvent.getGuild();
        long guildID = guild.getIdLong();
        List<Long> joinRoles = JoinRolesCommandInterface.getListOfJoinRoles(guildID);

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
                    match = reactRole.getEmoteID() == reaction.getReactionEmote().getEmote().getIdLong();
                }else{
                    match = reactRole.getEmoji().equals(reaction.getReactionEmote().getEmoji());
                }
                if (reactRole.getChannelID() == reaction.getChannel().getIdLong()
                        && reactRole.getMessageID() == reaction.getMessageIdLong()
                        && match) {
                    guild.addRoleToMember(reaction.getMember(), guild.getRoleById(reactRole.getRoleID())).queue();
                    Utils.sendPrivateMessage(reaction.getUser(), "You have been given the role " + guild.getRoleById(reactRole.getRoleID()).getName() + " in the server " + guild.getName());
                }
            }
        }

    }
}
