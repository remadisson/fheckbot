package de.remadisson.dcfheck.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.files;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.BlockingQueue;
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
            System.out.println("Initiate: Now playing: " + info.title + " - " + info.author + "(" + files.songLength(info.length) + ") | Queue " + queue.size());
        }
    }

    public AudioTrackInfo skipTopSongIndex(int Index){
        int countingIndex = 0;
        AudioTrack track = null;
        if(Index > this.queue.size()) Index = (this.queue.size()-1);
        for(AudioTrack audioTrack : this.queue){
            if(countingIndex == Index-1){
                track = this.queue.poll();
                this.audioPlayer.startTrack(track, false);
                Main.jda.getPresence().setActivity(Activity.listening(track.getInfo().title + " - " + track.getInfo().author));
                System.out.println("Skip: Initiate: Now playing: " + track.getInfo().title + " - " + track.getInfo().author + "(" + files.songLength(track.getInfo().length) + ") | Queue " + queue.size());
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
            System.out.println("Stopped playing");
            Main.jda.getPresence().setActivity(Activity.watching("DUCK YOU!"));
            PlayerManager.getINSTANCE().disconnectAudio(Main.guild);
            return;
        }

        AudioTrack track = this.queue.poll();
        this.audioPlayer.startTrack(track, false);

        assert track != null;
        AudioTrackInfo info = track.getInfo();
        Main.jda.getPresence().setActivity(Activity.listening(info.title + " - " + info.author));
        System.out.println("Now playing: " + info.title + " - " + info.author + "(" + files.songLength(info.length) + ") | Queue " + queue.size());
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        if(endReason.mayStartNext){
            nextTrack();
        } else {
           if(queue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
            Main.jda.getPresence().setActivity(Activity.watching("DUCK YOU!"));
            PlayerManager.getINSTANCE().stopAndClearPlaying(Main.guild);
           }
        }


    }


}
