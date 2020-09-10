package modbot.commands.customCommands;

import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;

import java.util.List;

public class CustomCommandSkeleton implements CommandInterface {
    private String handle;
    private String name;
    private String help;

    public CustomCommandSkeleton(String handle, String name, String help) {
        this.handle = handle;
        this.name = name;
        this.help = help;
    }


    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        ctx.getChannel().sendMessage(handle);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHelp() {
        return help;
    }
}
