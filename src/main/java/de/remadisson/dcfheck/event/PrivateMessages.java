package de.remadisson.dcfheck.event;

import de.remadisson.dcfheck.Main;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class PrivateMessages extends ListenerAdapter {

    ArrayList<String> friendIDs = new ArrayList<>(Arrays.asList("291269711394635776", "461923915791466497", "268362677313601536"));

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getChannelType() == ChannelType.PRIVATE){
            System.out.println("PRIVATE MESSAGE:" + event.getAuthor() + " - " + event.getMessage().getContentDisplay());
            if(event.getAuthor().isBot()) return;
            if(!friendIDs.contains(event.getAuthor().getId())){
                if(event.getAuthor().getMutualGuilds().get(0).getMemberById(event.getAuthor().getId()).getVoiceState().inAudioChannel()){
                    event.getAuthor().getMutualGuilds().get(0).moveVoiceMember(Main.jda.getGuilds().get(0).getMemberById(event.getAuthor().getId()), null).queue();
                }
            }
            event.getChannel().sendMessage("Fick dich.").queue();
        }
    }
}
