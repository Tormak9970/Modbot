package modbot.commands.customCommands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import modbot.CommandManager;
import modbot.commands.CommandInterface;
import modbot.utils.CustomCommand;
import modbot.utils.ReactionRoles;
import modbot.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class LoadCustomCommands {
    private static final Gson gson = new Gson();

    public static List<CommandInterface> LoadCCs(long guildId){
        List<CustomCommand> cc = new ArrayList<>();
        List<CommandInterface> ccsList = null;
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://localhost:8090/api/v1/modbot/database/customcomands/" + guildId);
            CloseableHttpResponse response = client.execute(request);
            String result;
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    InputStream iStream = entity.getContent();
                    result = Utils.convertStreamToString(iStream);
                    Type listType = new TypeToken<List<ReactionRoles>>() {}.getType();
                    cc = gson.fromJson(result, listType);
                    iStream.close();
                }
            } finally {
                client.close();
                response.close();
            }
            ccsList = convertCCsToSkeletons(cc, guildId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ccsList;
    }

    private static List<CommandInterface> convertCCsToSkeletons(List<CustomCommand> cc, long id){
        List<CommandInterface> ccsList = new ArrayList<>();
        for (CustomCommand cmd : cc){
            boolean nameFound = CommandManager.ccl.get(id).stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));
            if (!nameFound){
                ccsList.add(new CustomCommandSkeleton(cmd.getHandle(), cmd.getName(), cmd.getHelp()));
            }
        }

        return ccsList;
    }
}
