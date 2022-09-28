package de.remadisson.dcfheck.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.manager.CommandExecutor;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SkipCommand implements CommandExecutor {

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the current track, playing right now.";
    }

    @Override
    public List<OptionData> getOptionData() {
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "songindex", "Skip to a certain song in the queue.", false));
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Channel textChannel = event.getChannel();
        if(!textChannel.getId().equalsIgnoreCase(Main.botChannelID) && !textChannel.getId().equalsIgnoreCase("811511713475067904")) return;

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("Du musst in einem Voice-Channel sein, um mich zu benutzten.").queue(msg -> {
                msg.deleteOriginal().queueAfter(20, TimeUnit.SECONDS);
            });
            return;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            audioManager.openAudioConnection(vc);
        }

        if(!Objects.equals(event.getMember().getVoiceState().getChannel(), event.getGuild().getSelfMember().getVoiceState().getChannel()) && !event.getUser().getId().equals("268362677313601536")) return;

        if(event.getOption("songindex") != null) {
            int songIndex = Integer.parseInt(event.getOption("songindex").getAsString());
            if(songIndex <= 0){
                event.reply("Aufgrund des Mappings, kann der 0te Song nicht gefunden werden, da er sonst der -1te Song wäre. Pech gehabt. Versuch es doch einfach nochmal.").queue(msg -> {
                    msg.deleteOriginal().queueAfter(20, TimeUnit.SECONDS);
                });
                return;
            }
            AudioTrackInfo audioTrackInfo = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skipTopSongIndex(songIndex);
            event.reply("Jetzt spielt: `" + audioTrackInfo.title + "` von `" + audioTrackInfo.author + "`").queue();
        } else {
            PlayerManager.getINSTANCE().nextTrack(event);
        }
    }
}
