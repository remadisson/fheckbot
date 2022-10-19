package de.remadisson.dcfheck.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.api_remady.UpdateAPI;
import de.remadisson.dcfheck.enums.LogType;
import de.remadisson.dcfheck.files;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track){
        if(!this.audioPlayer.startTrack(track, true)){
            this.queue.offer(track);
        } else {
            AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
            Main.jda.getPresence().setActivity(Activity.listening(info.title + " - " + info.author));
            files.log(LogType.INFO, "Initiate: Now playing: " + info.title + " - " + info.author + "(" + files.longToFormattedLength(info.length) + ") | Queue " + queue.size());
        }
    }

    public AudioTrackInfo skipToSongIndex(int Index){
        /**
         * SKIPPING TO NEXT SONG (NEXT SONG) [UPDATE]
         */
        int countingIndex = 0;
        AudioTrack track = null;
        if(Index > this.queue.size()) Index = (this.queue.size());
        for(AudioTrack audioTrack : this.queue){
            if(countingIndex == Index-1){
                track = this.queue.poll();
                this.audioPlayer.startTrack(track, false);
                Main.jda.getPresence().setActivity(Activity.listening(track.getInfo().title + " - " + track.getInfo().author));
                files.log(LogType.INFO, "Skip: Initiate: Now playing: " + track.getInfo().title + " - " + track.getInfo().author + "(" + files.longToFormattedLength(track.getInfo().length) + ") | Queue " + queue.size());

                /**
                 * PUSHING UPDATE
                 */
                try {
                    UpdateAPI.sendAsyncUpdate("skip");
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return track.getInfo();
            } else {
                this.queue.remove(audioTrack);
            }
            countingIndex++;
        }
        return null;
    }

    public void nextTrack() {
        if(queue.isEmpty() && audioPlayer.getPlayingTrack() == null){
            /**
             * STOPPED PLAYING [UPDATE]
             */
            Main.jda.getPresence().setActivity(Activity.watching("DUCK YOU!"));
            PlayerManager.getINSTANCE().disconnectAudio(Main.guild);
            /**
             * PUSHING UPDATE
             */
            try {
                UpdateAPI.sendAsyncUpdate("stop1");
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        /**
         * NEXT TRACK (AUTOMATICALLY) [UPDATE]
         */

        AudioTrack track = this.queue.poll();
        this.audioPlayer.startTrack(track, false);
        assert track != null;
        AudioTrackInfo info = track.getInfo();
        Main.jda.getPresence().setActivity(Activity.listening(info.title + " - " + info.author));
        files.log(LogType.INFO, "Now playing: " + info.title + " - " + info.author + "(" + files.longToFormattedLength(info.length) + ") | Queue " + queue.size());

        /**
         * PUSHING UPDATE
         */
        try {
            UpdateAPI.sendAsyncUpdate("nextTrack");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        if(endReason.mayStartNext){
            nextTrack();
        } else {
            /**
             * SOMEWHAT UPDATE TOO XD [:(]
             */
           if(queue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
            Main.jda.getPresence().setActivity(Activity.watching("DUCK YOU!"));
            PlayerManager.getINSTANCE().stopAndClearPlaying(Main.guild);

               /**
                * PUSHING UPDATE
                */
               try {
                   UpdateAPI.sendAsyncUpdate("stop2");
               } catch (ExecutionException | InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        }


    }


}
