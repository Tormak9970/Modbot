package modbot.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandContext implements ICommandContext {
    private GuildMessageReceivedEvent event;
    private List<String> args;
    private EventWaiter waiter;

    public CommandContext(GuildMessageReceivedEvent event, List<String> args, EventWaiter waiter) {
        this.event = event;
        this.args = args;
        this.waiter = waiter;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    public List<String> getArgs() {
        return this.args;
    }

    public EventWaiter getWaiter(){
        return waiter;
    }
}
