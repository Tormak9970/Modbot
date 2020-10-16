package modbot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import modbot.commands.Listener;
import modbot.commands.roles.ReactionRolesCommand;
import modbot.utils.Utils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.*;

public class ModbotMain {

    private static final Gson gson = new Gson();
    private static int versionNum = -1;

    private static void run(){
        System.out.println("ran main line 32");
        int serverDVM = getDVM();
        System.out.println(serverDVM);
        if (versionNum != serverDVM && serverDVM != -1){
            versionNum = serverDVM;
            Utils.getFullGuilds();
        }
    }

    private static int getDVM(){
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpUriRequest request = RequestBuilder.get()
                    .setUri("http://localhost:8090/api/v1/modbot/database/dvm")
                    .build();
            CloseableHttpResponse response = client.execute(request);
            String result;
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    Type objType = new TypeToken<Integer>() {}.getType();
                    int temp = gson.fromJson(result, objType);
                    iStream.close();

                    return temp;

                }
            } finally {
                client.close();
                response.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void main(String[] args) throws LoginException, IOException {
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

        ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();
        Runnable runnableTask = ModbotMain::run;
        //executorService.scheduleWithFixedDelay(runnableTask, 0, 1, TimeUnit.MINUTES);
        executorService.scheduleWithFixedDelay(runnableTask, 0, 1, TimeUnit.HOURS);
    }
}
