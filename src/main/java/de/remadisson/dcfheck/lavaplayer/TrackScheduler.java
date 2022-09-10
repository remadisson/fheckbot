package de.remadisson.dcfheck.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.remadisson.dcfheck.Main;
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
            System.out.println("1 Now playing: " + info.title + " - " + info.author + "(" + String.format("%02d:%02d:%02d", (int) info.length / 1000 / 60 / 60, (int) info.length / 1000 / 60, (int) info.length / 1000 - ((int) info.length / 1000 / 60 * 100)) + ")");
        }
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
        System.out.println("2 Now playing: " + info.title + " - " + info.author + "("+ String.format("%02d:%02d:%02d", (int) info.length/1000/60/60, (int) info.length/1000/60, (int) info.length/1000-((int) info.length/1000/60*100)) + ")");


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
