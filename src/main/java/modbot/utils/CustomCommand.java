package modbot.utils;

public class CustomCommand {
    private String handle;
    private String name;
    private String help;


    public CustomCommand(String handle, String name, String help) {
        this.handle = handle;
        this.name = name;
        this.help = help;
    }

    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }
}
