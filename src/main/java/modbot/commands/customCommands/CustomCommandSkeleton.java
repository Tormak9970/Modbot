package modbot.commands.customCommands;

import modbot.CommandManager;
import modbot.commands.CommandContext;
import modbot.commands.CommandInterface;
import modbot.commands.SetPrefixCommand;
import modbot.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomCommandSkeleton implements CommandInterface {
    private String handle;
    private final String name;
    private final String help;
    private final List<CCVariables> ccl = Arrays.asList(CCVariables.values());
    private boolean silent;
    private List<Long> rRIds = new ArrayList<>();
    private List<Long> bLRIds = new ArrayList<>();
    private List<Long> bLCIds = new ArrayList<>();
    private Long rCId = null;
    private long responseCId;
    private boolean hasSetValues = false;

    public CustomCommandSkeleton(String handle, String name, String help) {
        this.handle = handle;
        this.name = name;
        this.help = help;
    }


    @Override
    public void handle(CommandContext ctx) {
        List<String> args = Utils.parse(this.handle);
        List<String> cclAsString = new ArrayList<>();
        ccl.forEach(CCVariable -> cclAsString.add(CCVariable.toString()));
        int nCounter = 0;
        boolean sendMsg = true;
        String choice = null;
        boolean hasRunDelete = false;
        boolean dmRes = false;
        boolean chRes = false;

        if (!hasSetValues){
            silent = false;
            responseCId = ctx.getChannel().getIdLong();
            for (int i = 0; i < args.size(); i++){
                String word = args.get(i);
                if(!sendMsg){
                    break;
                }
                if (Arrays.stream(CCVariables.values()).anyMatch((it) -> it.toString().equalsIgnoreCase(word))){

                    CCVariables ch = ccl.get(cclAsString.indexOf(word));
                    switch (ch) {
                        case USER -> args.set(i, ctx.getMember().getEffectiveName());
                        case MENTION_ROLE -> {
                            args.set(i, ctx.getGuild().getRolesByName(args.get(i + 1), true).get(0).getAsMention());
                            args.remove(i + 1);
                            i--;
                        }
                        case LINK_CHANNEL -> {
                            args.set(i, ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getAsMention());
                            args.remove(i + 1);
                            i--;
                        }
                        case MENTION_EVERYONE -> args.set(i, "@everyone");
                        case MENTION_HERE -> args.set(i, "@here");
                        case USER_ID -> args.set(i, ctx.getMember().getId());
                        case USER_NAME -> args.set(i, ctx.getMember().getUser().getName() + "#" + ctx.getMember().getUser().getDiscriminator());
                        case USER_USERNAME -> args.set(i, ctx.getMember().getUser().getName());
                        case USER_DISCRM -> args.set(i, "#" + ctx.getMember().getUser().getDiscriminator());
                        case USER_NICK -> args.set(i, ctx.getMember().getNickname());
                        case USER_GAME -> args.set(i, ctx.getMember().getActivities().get(0).getName());
                        case USER_AVATAR -> args.set(i, ctx.getMember().getUser().getAvatarUrl());
                        case USER_MENTION -> args.set(i, ctx.getMember().getAsMention());
                        case USER_ACC_CREATION_DATE -> args.set(i, ctx.getMember().getUser().getTimeCreated().getMonth() + " " + ctx.getMember().getUser().getTimeCreated().getDayOfMonth() + ", " + ctx.getMember().getUser().getTimeCreated().getYear());
                        case USER_SERVER_JOINED_DATE -> args.set(i, ctx.getMember().getTimeJoined().getMonth() + " " + ctx.getMember().getTimeJoined().getDayOfMonth() + " " + ctx.getMember().getTimeJoined().getYear());
                        case SERVER_ID -> args.set(i, ctx.getGuild().getId());
                        case SERVER_NAME -> args.set(i, ctx.getGuild().getName());
                        case SERVER_ICON -> args.set(i, ctx.getGuild().getIconUrl());
                        case SERVER_MEMBER_COUNT -> args.set(i, "" + ctx.getGuild().getMemberCount());
                        case SERVER_OWNER_ID -> args.set(i, ctx.getGuild().getOwnerId());
                        case SERVER_CREATION_DATE -> args.set(i, ctx.getGuild().getTimeCreated().getMonth() + " " + ctx.getGuild().getTimeCreated().getDayOfMonth() + " " + ctx.getGuild().getTimeCreated().getYear());
                        case SERVER_REGION -> args.set(i, ctx.getGuild().getRegionRaw());
                        case CHANNEL_ID -> args.set(i, ctx.getChannel().getId());
                        case CHANNEL_NAME -> args.set(i, ctx.getChannel().getName());
                        case CHANNEL_MENTION -> args.set(i, ctx.getChannel().getAsMention());
                        case TIME_24 -> {
                            Calendar cal = Calendar.getInstance();
                            args.set(i, "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
                        }
                        case TIME_12 -> {
                            Calendar cal = Calendar.getInstance();
                            args.set(i, "" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
                        }
                        case DATE -> {
                            args.set(i, "" + java.time.LocalDate.now());
                        }
                        case DATE_AND_TIME_24 -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss aa");
                            LocalDateTime now = LocalDateTime.now();
                            args.set(i, dtf.format(now));
                        }
                        case DATE_AND_TIME_12 -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            args.set(i, dtf.format(now));
                        }
                        case OUTPUT_COMMAND_PREFIX -> args.set(i, SetPrefixCommand.getPrefix(ctx.getGuild().getIdLong()));
                        case DELETE_COMMAND_MSG -> {
                            if (!hasRunDelete){
                                Utils.deleteHistory(1, ctx.getChannel());
                                args.remove(i);
                                i--;
                                hasRunDelete = true;
                            } else {
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.DELETE_COMMAND_MSG.toString() + " more then once").queue();
                                sendMsg = false;
                            }
                        }
                        case DISABLE_BOT_RESPONSE -> {
                            silent = true;
                            args.remove(i);
                            i--;
                        }
                        case INPUT_PARAM -> {
                            try {
                                args.set(i, ctx.getArgs().get(nCounter));
                                nCounter++;
                            } catch (IndexOutOfBoundsException e){
                                ctx.getChannel().sendMessage("please provide the proper amount of arguements.").queue();
                                sendMsg = false;
                            }
                        }
                        case SET_REQUIRED_ROLE -> {
                            if (bLRIds.size() > 0){
                                long val = Long.parseLong(args.get(i + 1));
                                if (bLRIds.stream().anyMatch((it) -> it == val)){
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You can't require a black listed role").queue();
                                } else {
                                    rRIds.add(ctx.getGuild().getRolesByName(args.get(i + 1), true).get(0).getIdLong());
                                    args.remove(i);
                                    args.remove(i + 1);
                                    i -= 2;
                                }
                            } else {
                                rRIds.add(ctx.getGuild().getRolesByName(args.get(i + 1), true).get(0).getIdLong());
                                args.remove(i);
                                args.remove(i + 1);
                                i -= 2;
                            }
                        }
                        case SET_REQUIRED_CHANNEL -> {
                            if (bLCIds.size() > 0){
                                long val = Long.parseLong(args.get(i + 1));
                                if (bLCIds.stream().anyMatch((it) -> it == val)){
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You can't require a black listed channel").queue();
                                } else {
                                    if (rCId == null){
                                        rCId = ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getIdLong();
                                        args.remove(i);
                                        args.remove(i + 1);
                                        i -= 2;
                                    }
                                }
                            } else {
                                if (rCId == null){
                                    rCId = ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getIdLong();
                                    args.remove(i);
                                    args.remove(i + 1);
                                    i -= 2;
                                } else {
                                    ctx.getChannel().sendMessage("You can't require more then one channel").queue();
                                }
                            }
                        }
                        case BLACKLIST_ROLE -> {
                            if (rRIds.size() > 0){
                                long val = Long.parseLong(args.get(i + 1));
                                if (rRIds.stream().anyMatch((it) -> it == val)){
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You can't black list a required role").queue();
                                } else {
                                    bLRIds.add(ctx.getGuild().getRolesByName(args.get(i + 1), true).get(0).getIdLong());
                                    args.remove(i);
                                    args.remove(i + 1);
                                    i -= 2;
                                }
                            } else {
                                bLRIds.add(ctx.getGuild().getRolesByName(args.get(i + 1), true).get(0).getIdLong());
                                args.remove(i);
                                args.remove(i + 1);
                                i -= 2;
                            }
                        }
                        case BLACKLIST_CHANNEL -> {
                            if (rCId != null){
                                long val = Long.parseLong(args.get(i + 1));
                                if (rCId == val){
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You can't black list a required channel").queue();
                                } else {
                                    bLCIds.add(ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getIdLong());
                                    args.remove(i);
                                    args.remove(i + 1);
                                    i -= 2;
                                }
                            } else {
                                bLCIds.add(ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getIdLong());
                                args.remove(i);
                                args.remove(i + 1);
                                i -= 2;
                            }

                        }
                        /*
                        case COMMAND -> {
                            if (args.get(i).equals(this.name)){
                                sendMsg = false;
                                ctx.getChannel().sendMessage("Warning! You have triggered a recursive loop. You cant call this command inside of this command").queue();
                            } else {
                                CommandManager manager = new CommandManager();
                                String cmdName = args.get(i);
                                if (manager.getCommandsList().stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmdName)) || manager.getCCommandsList(ctx.getGuild().getIdLong()).stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmdName))){
                                    CommandInterface cmd = manager.getCommand(args.get(i), ctx.getGuild().getIdLong());
                                    CommandContext cx = new CommandContext(ctx.getEvent(), ctx.getArgs());
                                    cmd.handle(cx);
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("Requested command does not exist").queue();
                                }
                            }
                        }
                         */
                        case SEND_RESPONSE_IN_DM -> {
                            if (!chRes){
                                if (responseCId == ctx.getChannel().getIdLong()){
                                    args.remove(i);
                                    args.remove(i + 1);
                                    dmRes = true;
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString() + " more the once.").queue();
                                }
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_DM.toString() + " and " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString()).queue();
                            }
                        }
                        case SEND_RESPONSE_IN_CHANNEL -> {
                            if (!dmRes){
                                if (responseCId == ctx.getChannel().getIdLong()){
                                    responseCId = ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getIdLong();
                                    args.remove(i);
                                    args.remove(i + 1);
                                    chRes = true;
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString() + " more the once.").queue();
                                }
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString() + " and " + CCVariables.SEND_RESPONSE_IN_DM.toString()).queue();
                            }
                        }
                        case LIST_CHOICES -> {
                            if (args.get(i + 1).indexOf(";") == args.get(i + 1).length() - 1){
                                String c1 = args.get(i + 1).substring(0, args.get(i + 1).length() - 2);

                                if (args.get(i + 2).indexOf(";") == args.get(i + 2).length() - 1){
                                    String c2 = args.get(i + 2).substring(0, args.get(i + 2).length() - 2);

                                    if (args.get(i + 3).indexOf(";") == args.get(i + 3).length() - 1){
                                        String c3 = args.get(i + 3).substring(0, args.get(i + 3).length() - 2);

                                        if (choice == null){
                                            int ranNum = (int)(Math.random() * 3);
                                            if (ranNum == 1){
                                                choice = c1;
                                            } else if (ranNum == 2){
                                                choice = c2;
                                            } else {
                                                choice = c3;
                                            }
                                            args.remove(i);
                                            args.remove(i + 1);
                                            args.remove(i + 2);
                                            args.remove(i + 3);
                                            i -= 4;
                                        } else {
                                            ctx.getChannel().sendMessage("You can't use choice more then once").queue();
                                        }
                                    } else {
                                        sendMsg = false;
                                        ctx.getChannel().sendMessage("You cant use " + CCVariables.LIST_CHOICES.toString() + " without 3 choices.").queue();
                                    }
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You cant use " + CCVariables.LIST_CHOICES.toString() + " without 3 choices.").queue();
                                }
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.LIST_CHOICES.toString() + " without 3 choices.").queue();
                            }
                        }
                        case GET_CHOICE -> {
                            if (choice != null){
                                args.set(i, choice);
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.GET_CHOICE.toString() + " without specifying choices.").queue();
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < args.size(); i++){
                String word = args.get(i);
                if(!sendMsg){
                    break;
                }
                if (Arrays.stream(CCVariables.values()).anyMatch((it) -> it.toString().equalsIgnoreCase(word))){

                    CCVariables ch = ccl.get(cclAsString.indexOf(word));
                    switch (ch) {
                        case USER -> args.set(i, ctx.getMember().getEffectiveName());
                        case MENTION_ROLE -> {
                            args.set(i, ctx.getGuild().getRolesByName(args.get(i + 1), true).get(0).getAsMention());
                            args.remove(i + 1);
                            i--;
                        }
                        case LINK_CHANNEL -> {
                            args.set(i, ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getAsMention());
                            args.remove(i + 1);
                            i--;
                        }
                        case USER_ID -> args.set(i, ctx.getMember().getId());
                        case USER_NAME -> args.set(i, ctx.getMember().getUser().getName() + "#" + ctx.getMember().getUser().getDiscriminator());
                        case USER_USERNAME -> args.set(i, ctx.getMember().getUser().getName());
                        case USER_DISCRM -> args.set(i, "#" + ctx.getMember().getUser().getDiscriminator());
                        case USER_NICK -> args.set(i, ctx.getMember().getNickname());
                        case USER_GAME -> args.set(i, ctx.getMember().getActivities().get(0).getName());
                        case USER_AVATAR -> args.set(i, ctx.getMember().getUser().getAvatarUrl());
                        case USER_MENTION -> args.set(i, ctx.getMember().getAsMention());
                        case USER_ACC_CREATION_DATE -> args.set(i, ctx.getMember().getUser().getTimeCreated().getMonth() + " " + ctx.getMember().getUser().getTimeCreated().getDayOfMonth() + ", " + ctx.getMember().getUser().getTimeCreated().getYear());
                        case USER_SERVER_JOINED_DATE -> args.set(i, ctx.getMember().getTimeJoined().getMonth() + " " + ctx.getMember().getTimeJoined().getDayOfMonth() + " " + ctx.getMember().getTimeJoined().getYear());
                        case SERVER_ID -> args.set(i, ctx.getGuild().getId());
                        case SERVER_NAME -> args.set(i, ctx.getGuild().getName());
                        case SERVER_ICON -> args.set(i, ctx.getGuild().getIconUrl());
                        case SERVER_MEMBER_COUNT -> args.set(i, "" + ctx.getGuild().getMemberCount());
                        case SERVER_OWNER_ID -> args.set(i, ctx.getGuild().getOwnerId());
                        case SERVER_REGION -> args.set(i, ctx.getGuild().getRegionRaw());
                        case CHANNEL_ID -> args.set(i, ctx.getChannel().getId());
                        case CHANNEL_NAME -> args.set(i, ctx.getChannel().getName());
                        case CHANNEL_MENTION -> args.set(i, ctx.getChannel().getAsMention());
                        case TIME_24 -> {
                            Calendar cal = Calendar.getInstance();
                            args.set(i, "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
                        }
                        case TIME_12 -> {
                            Calendar cal = Calendar.getInstance();
                            args.set(i, "" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
                        }
                        case DATE -> {
                            args.set(i, "" + java.time.LocalDate.now());
                        }
                        case DATE_AND_TIME_24 -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss aa");
                            LocalDateTime now = LocalDateTime.now();
                            args.set(i, dtf.format(now));
                        }
                        case DATE_AND_TIME_12 -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            args.set(i, dtf.format(now));
                        }
                        case OUTPUT_COMMAND_PREFIX -> args.set(i, SetPrefixCommand.getPrefix(ctx.getGuild().getIdLong()));
                        case DELETE_COMMAND_MSG -> {
                            if (!hasRunDelete){
                                Utils.deleteHistory(1, ctx.getChannel());
                                args.remove(i);
                                i--;
                                hasRunDelete = true;
                            } else {
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.DELETE_COMMAND_MSG.toString() + " more then once").queue();
                                sendMsg = false;
                            }
                        }
                        case DISABLE_BOT_RESPONSE -> {
                            silent = true;
                            args.remove(i);
                            i--;
                        }
                        case INPUT_PARAM -> {
                            try {
                                args.set(i, ctx.getArgs().get(nCounter));
                                nCounter++;
                            } catch (IndexOutOfBoundsException e){
                                ctx.getChannel().sendMessage("please provide the proper amount of arguements.").queue();
                                sendMsg = false;
                            }
                        }
                        /*
                        case COMMAND -> {
                            if (args.get(i).equals(this.name)){
                                sendMsg = false;
                                ctx.getChannel().sendMessage("Warning! You have triggered a recursive loop. You cant call this command inside of this command").queue();
                            } else {
                                CommandManager manager = new CommandManager();
                                String cmdName = args.get(i);
                                if (manager.getCommandsList().stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmdName)) || manager.getCCommandsList(ctx.getGuild().getIdLong()).stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmdName))){
                                    CommandInterface cmd = manager.getCommand(args.get(i), ctx.getGuild().getIdLong());
                                    CommandContext cx = new CommandContext(ctx.getEvent(), ctx.getArgs());
                                    cmd.handle(cx);
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("Requested command does not exist").queue();
                                }
                            }
                        }
                         */
                        case SEND_RESPONSE_IN_DM -> {
                            if (!chRes){
                                if (responseCId == ctx.getChannel().getIdLong()){
                                    args.remove(i);
                                    args.remove(i + 1);
                                    dmRes = true;
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString() + " more the once.").queue();
                                }
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_DM.toString() + " and " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString()).queue();
                            }
                        }
                        case SEND_RESPONSE_IN_CHANNEL -> {
                            if (!dmRes){
                                if (responseCId == ctx.getChannel().getIdLong()){
                                    responseCId = ctx.getGuild().getTextChannelsByName(args.get(i + 1), true).get(0).getIdLong();
                                    args.remove(i);
                                    args.remove(i + 1);
                                    chRes = true;
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString() + " more the once.").queue();
                                }
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.SEND_RESPONSE_IN_CHANNEL.toString() + " and " + CCVariables.SEND_RESPONSE_IN_DM.toString()).queue();
                            }
                        }
                        case LIST_CHOICES -> {
                            if (args.get(i + 1).indexOf(";") == args.get(i + 1).length() - 1){
                                String c1 = args.get(i + 1).substring(0, args.get(i + 1).length() - 2);

                                if (args.get(i + 2).indexOf(";") == args.get(i + 2).length() - 1){
                                    String c2 = args.get(i + 2).substring(0, args.get(i + 2).length() - 2);

                                    if (args.get(i + 3).indexOf(";") == args.get(i + 3).length() - 1){
                                        String c3 = args.get(i + 3).substring(0, args.get(i + 3).length() - 2);

                                        if (choice == null){
                                            int ranNum = (int)(Math.random() * 3);
                                            if (ranNum == 1){
                                                choice = c1;
                                            } else if (ranNum == 2){
                                                choice = c2;
                                            } else {
                                                choice = c3;
                                            }
                                            args.remove(i);
                                            args.remove(i + 1);
                                            args.remove(i + 2);
                                            args.remove(i + 3);
                                            i -= 4;
                                        } else {
                                            ctx.getChannel().sendMessage("You can't use choice more then once").queue();
                                        }
                                    } else {
                                        sendMsg = false;
                                        ctx.getChannel().sendMessage("You cant use " + CCVariables.LIST_CHOICES.toString() + " without 3 choices.").queue();
                                    }
                                } else {
                                    sendMsg = false;
                                    ctx.getChannel().sendMessage("You cant use " + CCVariables.LIST_CHOICES.toString() + " without 3 choices.").queue();
                                }
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.LIST_CHOICES.toString() + " without 3 choices.").queue();
                            }
                        }
                        case GET_CHOICE -> {
                            if (choice != null){
                                args.set(i, choice);
                            } else {
                                sendMsg = false;
                                ctx.getChannel().sendMessage("You cant use " + CCVariables.GET_CHOICE.toString() + " without specifying choices.").queue();
                            }
                        }
                    }
                }
            }
        }
        if (sendMsg){
            hasSetValues = true;
            if (rRIds.size() > 0){
                for (long id : rRIds){
                    if (!ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(id))){
                        sendMsg = false;
                        break;
                    }
                }
            }
            if (sendMsg){
                if (bLRIds.size() > 0){
                    for (long id : bLRIds){
                        if (ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(id))){
                            sendMsg = false;
                            break;
                        }
                    }
                }
                if (sendMsg){
                    if (bLCIds.size() > 0){
                        for (long id : bLCIds){
                            if (ctx.getChannel().getIdLong() == id){
                                sendMsg = false;
                                break;
                            }
                        }
                    }
                    if (sendMsg){
                        if (rCId != null && !(ctx.getChannel().getIdLong() == rCId)){
                            sendMsg = false;
                        }
                    }
                }
            }
        }
        if (sendMsg){
            if (!silent){
                StringJoiner tr = new StringJoiner(" ");
                for (String word : args){
                    tr.add(word);
                }
                ctx.getGuild().getTextChannelById(responseCId).sendMessage(tr.toString()).queue();
            }
        }
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
