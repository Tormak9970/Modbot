package modbot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import modbot.commands.Listener;
import modbot.commands.roles.ReactionRolesCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class ModbotMain {

    public static void main(String[] args) throws LoginException, IOException, SQLException {
        String token;

        InputStream in = ModbotMain.class.getClassLoader().getResourceAsStream("token.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        token = reader.readLine();
        EventWaiter waiter = new EventWaiter();

        DefaultShardManagerBuilder.createDefault(token)
                .setActivity(Activity.playing("Modding EPC"))
                .addEventListeners(waiter)
                .addEventListeners(new ReactionRolesCommand(waiter))
                .addEventListeners(new Listener(waiter))
                .build();




    }
}
