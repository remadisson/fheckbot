package de.remadisson.dcfheck.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.remadisson.dcfheck.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager(){
       this.musicManagers = new HashMap<>();
       this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String[] args, String trackURL, boolean isSearch){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                textChannel.sendMessage("Der Track `" + audioTrack.getInfo().title + "` von `" + audioTrack.getInfo().author + "` wurde der Playlist hinzugefügt.").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                if (isSearch && !tracks.isEmpty()) {
                    musicManager.scheduler.queue(tracks.get(0));
                    textChannel.sendMessage("Der Track `" + tracks.get(0).getInfo().title + "` von `" + tracks.get(0).getInfo().author + "` wurde der Queue hinzugefügt.").queue();
                } else if(!isSearch){
                    for(AudioTrack track : tracks){
                        musicManager.scheduler.queue(track);
                    }
                    System.out.println("Added " + tracks.size() + " to the queue");
                    textChannel.sendMessage("Hinzufügen von  `" + tracks.size() + "` Tracks von der Playlist `" + audioPlaylist.getName() + "`").queue();
                }


            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Mit der Suche `" + String.join(" ", args) + "` konnte nichts angefangen werden.").queue();
                System.out.println("No match found.");
            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }


    public void stopAndClearPlaying(Guild guild){
        final GuildMusicManager gm = PlayerManager.getINSTANCE().getMusicManager(guild);
        gm.scheduler.audioPlayer.stopTrack();
        gm.scheduler.queue.clear();
        disconnectAudio(guild);
    }
    public void nextTrack(TextChannel textChannel){
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        if(musicManager.scheduler.queue.isEmpty()) {
            textChannel.sendMessage("Die Playlist ist leer.").queue();
            stopAndClearPlaying(textChannel.getGuild());
            return;
        }

        musicManager.scheduler.nextTrack();
        textChannel.sendMessage("Jetzt spielt: `" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "` von `" + musicManager.audioPlayer.getPlayingTrack().getInfo().author + "`").queue();
    }

    public void disconnectAudio(Guild guild){
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
        Main.jda.getPresence().setActivity(Activity.watching("DUCK YOU!"));
    }

    public static PlayerManager getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }


}