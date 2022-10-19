package de.remadisson.dcfheck.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.api_remady.UpdateAPI;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    public GuildMusicManager getMusicManager(){
        Guild guild = Main.guild;
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) -> {

            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(SlashCommandInteractionEvent event, String args, String trackURL, boolean isSearch){
        final GuildMusicManager musicManager = this.getMusicManager();
        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                event.reply("Der Track `" + audioTrack.getInfo().title + "` von `" + audioTrack.getInfo().author + "` wurde der Playlist hinzugefügt. (" + musicManager.scheduler.queue.size() + " in der Queue)").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                if (isSearch && !tracks.isEmpty()) {
                    musicManager.scheduler.queue(tracks.get(0));
                    event.reply("Der Track `" + tracks.get(0).getInfo().title + "` von `" + tracks.get(0).getInfo().author + "` wurde der Queue hinzugefügt.").queue();
                } else if(!isSearch){
                    for(AudioTrack track : tracks){
                        musicManager.scheduler.queue(track);
                    }
                    event.reply("Hinzufügen von  `" + tracks.size() + "` Tracks von der Playlist `" + audioPlaylist.getName() + "`\nJetzt spielt: `" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "` von `" + musicManager.audioPlayer.getPlayingTrack().getInfo().author + "`").queue();
                }
                /**
                 * PUSHING UPDATE
                 */
                try {
                    UpdateAPI.sendAsyncUpdate("initiate");
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void noMatches() {
                event.reply("Mit der Suche `" + args + "` konnte nichts angefangen werden.").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                
            }
        });
    }

    /**
     * STOP
     * @param guild
     */
    public void stopAndClearPlaying(Guild guild){
        final GuildMusicManager gm = PlayerManager.getINSTANCE().getMusicManager();
        gm.scheduler.audioPlayer.stopTrack();
        gm.scheduler.queue.clear();
        disconnectAudio(guild);

        /**
         * PUSHING UPDATE
         */
        try {
            UpdateAPI.sendAsyncUpdate("stop3");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void nextTrack(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager();
        if(musicManager.scheduler.queue.isEmpty()) {
            event.reply("Die Playlist ist leer.").queue();
            stopAndClearPlaying(Main.guild);
            return;
        }

        musicManager.scheduler.nextTrack();
        event.reply("Jetzt spielt: `" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "` von `" + musicManager.audioPlayer.getPlayingTrack().getInfo().author + "`").queue();
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
