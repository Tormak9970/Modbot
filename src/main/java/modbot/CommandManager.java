package modbot;

import modbot.commands.CommandContext;
import modbot.commands.HelpCommand;
import modbot.commands.CommandInterface;
import modbot.commands.SetPrefixCommand;
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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<CommandInterface> commands = new ArrayList<>();

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

    @Nullable
    public CommandInterface getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (CommandInterface cmd : this.commands) {
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
        CommandInterface cmd = this.getCommand(invoke);

        if (cmd != null) {
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
