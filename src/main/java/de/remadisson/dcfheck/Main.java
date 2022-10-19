package de.remadisson.dcfheck;

import de.remadisson.dcfheck.api_remady.UpdateAPI;
import de.remadisson.dcfheck.commands.*;
import de.remadisson.dcfheck.enums.LogType;
import de.remadisson.dcfheck.event.AuditLogger;
import de.remadisson.dcfheck.event.PrivateMessages;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.manager.CommandManager;
import de.remadisson.dcfheck.web.init;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;

public class Main extends ListenerAdapter {

    public static String botChannelID = null;
    public static JDA jda;
    public static Guild guild;

    public static Dotenv dotenv;
    private static final String tokenFhot = "";

    public static ExecutorService executor = Executors.newFixedThreadPool(20);
    public static void main(String[] args){
        dotenv = Dotenv.configure().load();
        JDABuilder builder = JDABuilder.createDefault(args == null ? tokenFhot : (args.length == 0 ? tokenFhot : (args[0] == null ? tokenFhot : args[0])));

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.watching("DUCK YOU!"));

        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableCache(CacheFlag.CLIENT_STATUS);
        builder.enableIntents(GatewayIntent.GUILD_INVITES);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        //builder.enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);

        builder.setEventPassthrough(true);

        CommandManager commandManager = new CommandManager();
        commandManager.add(new PlayCommand());
        commandManager.add(new ClearCommand());
        commandManager.add(new SkipCommand());
        commandManager.add(new StopCommand());
        builder.addEventListeners(new AuditLogger());
        builder.addEventListeners(new Main());
        builder.addEventListeners(commandManager);
        builder.addEventListeners(new PrivateMessages());


        try {
          jda = builder.build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendEmbedMessage(TextChannel channel, EmbedBuilder eb){
        channel.sendMessage("").setEmbeds(eb.build()).queue();
    }

    @Override
    public void onReady(ReadyEvent e) {
        System.out.println(new GregorianCalendar().getTime().toLocaleString() + " > Ready!");
        guild = jda.getGuildById("763096399071674438");

        if (botChannelID == null) {
            botChannelID = jda.getTextChannelsByName("bot-commands", false).get(0).getId();
        }

        init.onExpress();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PlayerManager.getINSTANCE().stopAndClearPlaying(guild);
        }));
    }
}
