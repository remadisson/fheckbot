package de.remadisson.dcfheck.web;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.remadisson.dcfheck.enums.LogType;
import de.remadisson.dcfheck.files;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import express.Express;
import express.middleware.Middleware;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

public class init {

    public static void onExpress(){
        Express app = new Express();

        app.use(Middleware.cors());

        app.get("/queue", ((req, res) -> {
            AudioTrack current = PlayerManager.getINSTANCE().getMusicManager().scheduler.audioPlayer.getPlayingTrack();

            BlockingQueue<AudioTrack> queue = PlayerManager.getINSTANCE().getMusicManager().scheduler.queue;
            if(current == null){
                res.send(new JSONObject("{\"status\": 404, \"message\": \"Derzeit spielt kein Lied.\"}").toString());
                return;
            }

            res.send(getSongsAsJsonObject(current, queue).toString());

        }));

        app.get("/skip/:index", (req,res) ->{
            try {
                int index = Integer.parseInt(req.getParam("index"));

                if(PlayerManager.getINSTANCE().getMusicManager().scheduler.queue.isEmpty()){
                    res.send(new JSONObject().put("status", 400).put("message", "Bad Request: There is currently no song in the playlist.").toString());
                    System.out.println("Response: " + new JSONObject().put("status", 400).put("message", "Bad Request: There is currently no song in the playlist.").toString());
                    return;
                }

                if(PlayerManager.getINSTANCE().getMusicManager().audioPlayer.getPlayingTrack() == null){
                    res.send(new JSONObject().put("status", 400).put("message", "Bad Request: There is no queue that can be skipped!").toString());
                    System.out.println("Response: " + new JSONObject().put("status", 400).put("message", "Bad Request: There is no queue that can be skipped!").toString());
                    return;
                }

                PlayerManager.getINSTANCE().getMusicManager().scheduler.skipToSongIndex(index);
                files.log(LogType.INFO, "Express initated skip (" + index + ")");

                res.send(new JSONObject().put("status", 200).put("message", "Skipped to " + index + "!").toString());

            }catch(NumberFormatException ex){
                res.send(new JSONObject().put("status", 400).put("message", "Bad Request: " + req.getParam("index") + " is no number.").toString());
            }
        });

        app.use("/", ((request, response) -> {
            response.redirect("https://fhot.remady.me/");
        }));

        app.listen(8080);
    }

    public static JSONObject getSongsAsJsonObject(AudioTrack current, BlockingQueue<AudioTrack> queue){
        JSONObject object = new JSONObject();
        AudioTrackInfo currentInfo = current.getInfo();

        object.put("status", 200);
        object.put("currentlyPlaying", new JSONObject().put("title", currentInfo.title).put("author", currentInfo.author).put("length",files.longToFormattedLength(currentInfo.length)).put("link", currentInfo.uri));

        if(queue.isEmpty()){
            return object;
        }

        JSONArray queueJson = new JSONArray();

        for(int index = 0; index < queue.size(); index++){
            AudioTrackInfo queuedInfo = queue.stream().toList().get(index).getInfo();
            queueJson.put(index, new JSONObject().put("title", queuedInfo.title).put("author", queuedInfo.author).put("length", files.longToFormattedLength(queuedInfo.length)).put("link", queuedInfo.uri));
        }

        object.put("queue", queueJson);

        return object;

    }
}
