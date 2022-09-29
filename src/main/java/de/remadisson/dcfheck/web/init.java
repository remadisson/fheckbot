package de.remadisson.dcfheck.web;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.files;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import express.Express;
import express.utils.Status;
import org.json.*;

import java.util.concurrent.BlockingQueue;

public class init {

    public static JSONObject queuedJson = null;
    public static void onExpress(){
        Express app = new Express();

        app.get("/", ((req, res) -> {
            AudioTrack current = PlayerManager.getINSTANCE().getMusicManager(Main.guild).scheduler.audioPlayer.getPlayingTrack();
            BlockingQueue<AudioTrack> queue = PlayerManager.getINSTANCE().getMusicManager(Main.guild).scheduler.queue;
            if(current == null){
                res.send(new JSONObject("{\"status\": 404, \"message\": \"Derzeit ist nichts am spielen.\"}").toString());
                return;
            }

            res.send(getSongsAsJsonObject(current, queue).toString());
            res.sendStatus(Status._200);

        }));

        app.listen(8080);
        System.out.println("Express is running!");
    }

    public static JSONObject getSongsAsJsonObject(AudioTrack current, BlockingQueue<AudioTrack> queue){
        JSONObject object = new JSONObject();
        AudioTrackInfo currentInfo = current.getInfo();

        object.put("currentlyPlaying", new JSONObject().put("title", currentInfo.title).put("author", currentInfo.author).put("length",files.longToFormattedLength(currentInfo.length)).put("link", currentInfo.uri));

        JSONArray queueJson = new JSONArray();

        for(int index = 0; index < queue.size(); index++){
            AudioTrackInfo queuedInfo = queue.stream().toList().get(index).getInfo();
            queueJson.put(index,new JSONObject().put("Index of Queue: " + index, new JSONObject().put("title", queuedInfo.title).put("author", queuedInfo.author).put("length", files.longToFormattedLength(queuedInfo.length)).put("link", queuedInfo.uri)));
        }

        object.put("queue", queueJson);

        return object;

    }
}
