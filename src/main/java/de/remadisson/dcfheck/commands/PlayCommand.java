package de.remadisson.dcfheck.commands;

import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.manager.CInterface;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PlayCommand implements CInterface {

    public boolean isUrl(String link) {
        try{
            new URI(link);
            return true;
        } catch(URISyntaxException ex){
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays the Song/Playlist injected via Link or via search.";
    }

    @Override
    public List<OptionData> getOptionData() {
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "arg", "Just type your search or insert a link.", true));
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if(!textChannel.getId().equalsIgnoreCase(Main.botChannelID)) {event.reply("Du kannst hier diesen Command nicht benutzen!").queue(msg -> {
            msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
        }); return;}

        System.out.println();
        System.out.println(event.getCommandPath());
        String args = Objects.requireNonNull(event.getOption("arg")).getAsString();

        System.out.println(event.getUser().getName() + " ("+event.getUser().getId()+") used: '" +event.getCommandString() + " " + args + "'");

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("Du musst in einem Voice-Channel sein, um mich zu benutzten.").queue(msg -> {
                msg.deleteOriginal().queueAfter(30, TimeUnit.SECONDS);
            });
            return;
        }
        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            audioManager.openAudioConnection(vc);
        } else if(!Objects.equals(event.getMember().getVoiceState().getChannel(), event.getGuild().getSelfMember().getVoiceState().getChannel()) && !event.getUser().getId().equals("268362677313601536")) {
            return;
        }

        String link = args;
        boolean isUrl = isUrl(link);
        if(!isUrl){
            link = "ytsearch:" + link + " audio";
        }

        PlayerManager.getINSTANCE().loadAndPlay(event, args, link, !isUrl);
    }
}
