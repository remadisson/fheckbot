package de.remadisson.dcfheck.commands;

import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Objects;

public class StopCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if(!textChannel.getId().equalsIgnoreCase(Main.botChannelID)) return;

        String[] message = event.getMessage().getContentDisplay().split(" ");
        String arg0 = message[0];
        String indicator = arg0.split("")[0];
        String command = arg0.substring(1);
        String[] args = Arrays.copyOfRange(message, 1, message.length);

        //if(!indicator.equals(main.botCommandIndicator) && !event.getAuthor().getId().equals(event.getGuild().getSelfMember().getId())){ event.getMessage().delete().queue(); return; }
        if(!command.equalsIgnoreCase("stop")) return;
        System.out.println(event.getAuthor().getName() + " ("+event.getAuthor().getId()+") used: '" + event.getMessage().getContentDisplay() + "'");

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            textChannel.sendMessage("Du musst in einem Voice-Channel sein, um mich zu benutzten.").queue();
            return;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) return;

        if(!Objects.equals(event.getMember().getVoiceState().getChannel(), event.getGuild().getSelfMember().getVoiceState().getChannel()) && !event.getAuthor().getId().equals("268362677313601536")) return;

        PlayerManager.getINSTANCE().stopAndClearPlaying(textChannel.getGuild());

        textChannel.sendMessage("Musik-Widergabe wurde angehalten. (Queue ist leer)").queue();
    }

}
