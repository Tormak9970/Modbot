package modbot;

import modbot.commands.CommandContext;
import modbot.commands.HelpCommand;
import modbot.commands.CommandInterface;
import modbot.commands.SetPrefixCommand;
import modbot.commands.customCommands.CustomCommandSkeleton;
import modbot.commands.info.BotInfoCommand;
import modbot.commands.info.ServerInfoCommand;
import modbot.commands.info.UserInfoCommand;
import modbot.commands.moderation.BanCommand;
import modbot.commands.moderation.KickCommand;
import modbot.commands.moderation.MuteCommand;
import modbot.commands.moderation.bannedWords.BanWordCommand;
import modbot.commands.moderation.bannedWords.GetBannedWordsCommand;
import modbot.commands.moderation.bannedWords.RemoveBannedWord;
import modbot.commands.roles.JoinRolesCommand;
import modbot.commands.roles.RemoveJoinRoleCommand;
import modbot.utils.CustomCommand;
import modbot.utils.CustomGuildObj;
import modbot.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<CommandInterface> commands = new ArrayList<>();
    public static Map<Long, List<CommandInterface>> ccl = new HashMap<>();

    public CommandManager() {
        addCommand(new JoinRolesCommand());
        addCommand(new RemoveJoinRoleCommand());
        addCommand(new SetPrefixCommand());
        addCommand(new HelpCommand(this));
        addCommand(new GetBannedWordsCommand());
        addCommand(new BanWordCommand());
        addCommand(new RemoveBannedWord());
        addCommand(new MuteCommand());
        addCommand(new BanCommand());
        addCommand(new KickCommand());
        addCommand(new BotInfoCommand());
        addCommand(new ServerInfoCommand());
        addCommand(new UserInfoCommand());
    }

    private void addCommand(CommandInterface cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<CommandInterface> getCommandsList(){
        return commands;
    }

    public static List<CommandInterface> convertCCsToSkeletons(List<CustomCommand> cc, long id){
        List<CommandInterface> ccsList = new ArrayList<>();

        if (ccl.get(id) != null){
            for (CustomCommand cmd : cc){
                boolean nameFound = ccl.get(id).stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));
                if (!nameFound){
                    ccsList.add(new CustomCommandSkeleton(cmd.getHandle(), cmd.getName(), cmd.getHelp()));
                }
            }
        } else {
            for (CustomCommand cmd : cc){
                ccsList.add(new CustomCommandSkeleton(cmd.getHandle(), cmd.getName(), cmd.getHelp()));
            }
        }

        return ccsList;
    }

    @Nullable
    public CommandInterface getCommand(String search, long guildId) {
        String searchLower = search.toLowerCase();
        boolean isAloud = true;

        CustomGuildObj guild = Utils.fullGuilds.get(guildId);

        if (!guild.isSetPrefix() && searchLower.equals("prefix")){
            isAloud = false;
        } else if (!guild.isHelp() && searchLower.equals("help")) {
            isAloud = false;
        } else if (!guild.isBotInfo() && searchLower.equals("botinfo")) {
            isAloud = false;
        } else if (!guild.isServerInfo() && searchLower.equals("serverinfo")) {
            isAloud = false;
        } else if (!guild.isUserInfo() && searchLower.equals("userinfo")) {
            isAloud = false;
        } else if (!guild.isBanWord() && searchLower.equals("banword")) {
            isAloud = false;
        } else if (!guild.isGetBannedWords() && searchLower.equals("words")) {
            isAloud = false;
        } else if (!guild.isRemoveBannedWords() && searchLower.equals("removeword")) {
            isAloud = false;
        } else if (!guild.isBanUser() && searchLower.equals("ban")) {
            isAloud = false;
        } else if (!guild.isKickUser() && searchLower.equals("kick")) {
            isAloud = false;
        } else if (!guild.isMuteUser() && searchLower.equals("mute")) {
            isAloud = false;
        } else if (!guild.isJoinRole() && searchLower.equals("joinrole")) {
            isAloud = false;
        } else if (!guild.isRemoveJoinRole() && searchLower.equals("removejoinrole")) {
            isAloud = false;
        }

        for (CommandInterface cmd : this.commands) {
            if (cmd.getName().equals(searchLower) && isAloud) {
                return cmd;
            }
        }

        ccl.computeIfAbsent(guildId, s -> convertCCsToSkeletons(Utils.fullGuilds.get(guildId).getListOfCCs(), guildId));
        for (CommandInterface cmd : ccl.get(guildId)) {
            ccl.get(guildId).add(cmd);
            if (cmd.getName().equals(searchLower)) {
                return cmd;
            }
        }

        return null;
    }

    public void handle(GuildMessageReceivedEvent event) {
        long id = event.getGuild().getIdLong();
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(SetPrefixCommand.getPrefix(id)), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();

        CommandInterface cmd = this.getCommand(invoke, id);
        if (cmd != null) {
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
