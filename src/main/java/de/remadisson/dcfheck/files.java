package de.remadisson.dcfheck;

import de.remadisson.dcfheck.enums.LogType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class files {
    /**
     * Class to hold messy general information / methods.
     */

    public static String createLog(LogType logType, String message){
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss - dd/MM/YYYY");
        return dtf.format(new Date()) + " " + logType.getPrefix() + " " + message;
    }

    public static void log(LogType logType, String log){
        TextChannel channel = Main.guild.getTextChannelById("1014932313005625477");
        assert channel != null;
        ThreadChannel thread = null;
        if(!channel.getThreadChannels().stream().map(ThreadChannel::getName).toList().contains("cmd-logger")){
            thread = channel.createThreadChannel("cmd-logger").complete();
            thread.addThreadMemberById("268362677313601536").queue();
        }

        if(!channel.getThreadChannels().stream().map(ThreadChannel::getName).toList().contains("info")){
            thread = channel.createThreadChannel("info").complete();
            thread.addThreadMemberById("268362677313601536").queue();
        }

        if(!channel.getThreadChannels().stream().map(ThreadChannel::getName).toList().contains("warning")){
            thread = channel.createThreadChannel("warning").complete();
            thread.addThreadMemberById("268362677313601536").queue();
        }

        switch (logType) {
            case CMD -> {
                thread = channel.getThreadChannels().stream().filter(threadChannel -> threadChannel.getName().equalsIgnoreCase("cmd-logger")).toList().get(0);
                thread.sendMessage(log).queue();
            }
            case INFO -> {
                thread = channel.getThreadChannels().stream().filter(threadChannel -> threadChannel.getName().equalsIgnoreCase("info")).toList().get(0);
                thread.sendMessage(log).queue();
            }
            case WARNING -> {
                thread = channel.getThreadChannels().stream().filter(threadChannel -> threadChannel.getName().equalsIgnoreCase("warning")).toList().get(0);
                thread.sendMessage(log).queue();
            }
        }

        System.out.println(createLog(logType, log));

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
