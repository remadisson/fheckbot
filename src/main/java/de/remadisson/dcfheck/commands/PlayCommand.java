package de.remadisson.dcfheck.commands;

import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PlayCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if(!textChannel.getId().equalsIgnoreCase(Main.botChannelID)) return;

        String[] message = event.getMessage().getContentDisplay().split(" ");
        String arg0 = message[0];
        String indicator = arg0.split("")[0];
        String command = arg0.substring(1);
        String[] args = Arrays.copyOfRange(message, 1, message.length);

        if(!indicator.equals(Main.botCommandIndicator) && !event.getAuthor().getId().equals(event.getGuild().getSelfMember().getId())){ event.getMessage().delete().queue(); return; }
        if(!command.equalsIgnoreCase("play")) return;

        System.out.println(event.getAuthor().getName() + " ("+event.getAuthor().getId()+") used: '" + event.getMessage() + "'");

        if(!event.getMember().getVoiceState().inAudioChannel()) {

            textChannel.sendMessage("Du musst in einem Voice-Channel sein, um mich zu benutzten.").queue(msg -> {
                        msg.delete().queueAfter(20, TimeUnit.SECONDS);
                    });
            return;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            audioManager.openAudioConnection(vc);
        } else if(!Objects.equals(event.getMember().getVoiceState().getChannel(), event.getGuild().getSelfMember().getVoiceState().getChannel()) && !event.getAuthor().getId().equals("268362677313601536")) {
            return;
        }

        String link = String.join(" ", args);
        boolean isUrl = isUrl(link);
        if(!isUrl){
            link = "ytsearch:" + link + " audio";
        }

        PlayerManager.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), args, link, !isUrl);
    }

    public boolean isUrl(String link) {
        try{
            new URI(link);
            return true;
        } catch(URISyntaxException ex){
            return false;
        }
    }
}
