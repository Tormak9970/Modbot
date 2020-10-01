package modbot.commands;

import modbot.CommandManager;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand implements CommandInterface {

    private CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        long id = ctx.getGuild().getIdLong();
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            builder.append("List of commands\n");

            manager.getCommandsList().stream().map(CommandInterface::getName).forEach(
                    (it) -> builder.append('`').append(SetPrefixCommand.getPrefix(id)).append(it).append("`\n")
            );

            channel.sendMessage(builder.toString()).queue();
            return;
        }

        String search = args.get(0);
        /*CommandInterface command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("Nothing found for " + search).queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();*/
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows the list with commands in the bot\n" +
                "Usage: `!!help [command]`";
    }
}
