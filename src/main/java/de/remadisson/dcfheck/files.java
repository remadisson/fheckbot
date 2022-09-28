package de.remadisson.dcfheck;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class files {
    /**
     * Class to hold messy general information / methods.
     */

    public static String createLog(String message){
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss - dd/MM/YYYY");
        return dtf.format(new Date()) + " [LOG] " + message;
    }

    public static void commandLog(String log){
        TextChannel channel = Main.guild.getTextChannelById("1014932313005625477");
        assert channel != null;
        ThreadChannel thread = null;
        if(!channel.getThreadChannels().stream().map(ThreadChannel::getName).toList().contains("cmd-logger")){
            thread = channel.createThreadChannel("cmd-logger").complete();
            thread.addThreadMemberById("268362677313601536").queue();
        }

        thread = channel.getThreadChannels().stream().filter(threadChannel -> threadChannel.getName().equalsIgnoreCase("cmd-logger")).toList().get(0);
        thread.sendMessage(log).queue();
        System.out.println(createLog(log));

        /**
         * TODO REPLACE OLD "LOGGING" WITH NEW ONE, CREATING FILE API AND SAVE LOGS EVERY DAY
         */
    }

    public static String longToFormattedLength(long length){
        long second = (length / 1000) % 60;
        long minute = (length / (1000 * 60)) % 60;
        long hour = (length / (1000 * 60 * 60)) % 24;

       return String.format("%02d:%02d:%02d", hour, minute, second);
    }

}
