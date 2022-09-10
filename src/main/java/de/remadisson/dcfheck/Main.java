package de.remadisson.dcfheck;

import de.remadisson.dcfheck.commands.ClearCommand;
import de.remadisson.dcfheck.commands.PlayCommand;
import de.remadisson.dcfheck.commands.SkipCommand;
import de.remadisson.dcfheck.commands.StopCommand;
import de.remadisson.dcfheck.event.AuditLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {

    public static String botChannelID= "1015745887395397692";
    public static String botCommandIndicator = ";";
    public static JDA jda;
    public static Guild guild;

    public static void main(String[] args){
        //TODO token in ARGS instead of direct input from CODE

        JDABuilder builder = JDABuilder.createDefault("MTAxNDkzNDkxMzA3NTY1NDcwNg.GamqFm.0aJP93XFNyJiRVExl_wtn1cHsWbmGWDLMYcBZM");

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("DUCK YOU!"));

        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableCache(CacheFlag.CLIENT_STATUS);
        builder.enableIntents(GatewayIntent.GUILD_INVITES);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        //builder.enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        //builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);

        builder.setEventPassthrough(true);

        // Registering AuditLogger -> Logs AuditLogs into a channel.
        builder.addEventListeners(new AuditLogger());
        builder.addEventListeners(new PlayCommand());
        builder.addEventListeners(new SkipCommand());
        builder.addEventListeners(new StopCommand());
        builder.addEventListeners(new Main());
        builder.addEventListeners(new ClearCommand());


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
    public void onReady(ReadyEvent e){
        System.out.println("Ready!");
        guild = jda.getGuildById("763096399071674438");
    }

}
