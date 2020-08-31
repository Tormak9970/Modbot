package modbot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.function.Consumer;

public abstract class Utils{

    //example of restAction
    public static void sendPrivateMessage(User user, String content)
    {
        // notice that we are not placing a semicolon (;) in the callback this time!
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(content).queue() );
    }

    public static void deleteHistory(int numMsg, TextChannel channel){
        Consumer<List<Message>> callback = channel::purgeMessages;
        channel.getHistory().retrievePast(numMsg).queue(callback);
    }

    public static String getUptime(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        long uptimeInSeconds = uptime / 1000;
        long numberOfHours = uptimeInSeconds / (60 * 60);
        long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
        long numberOfSeconds = uptimeInSeconds % 60;

        return String.format("Uptime is `%s hours, %s minutes, %s seconds`",
                numberOfHours, numberOfMinutes, numberOfSeconds);
    }

}
