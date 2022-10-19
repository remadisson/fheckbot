package de.remadisson.dcfheck.api_remady;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.enums.LogType;
import de.remadisson.dcfheck.files;
import de.remadisson.dcfheck.lavaplayer.GuildMusicManager;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.web.init;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class UpdateAPI {

    private static String host = "localhost";
    private static int port = 8081;
    private static String api = "http://" + host + ":" + port + "/fhot";
    private static String updateSongs = "/queue";

    protected static boolean errorSend = false;

    public static String sendUpdate(String action, AudioTrack currentlyPlaying, BlockingQueue<AudioTrack> queue) throws IOException, InterruptedException {

        JSONObject body = null;
        Dotenv dotenv = Main.dotenv;

        if(currentlyPlaying != null) {
            if (PlayerManager.getINSTANCE().getMusicManager().scheduler.queue.isEmpty() && PlayerManager.getINSTANCE().getMusicManager().audioPlayer.getPlayingTrack() == null) {
                body = new JSONObject().put("status", 404).put("message", "Derzeit spielt kein Lied.");
            }

            if (PlayerManager.getINSTANCE().getMusicManager().audioPlayer.getPlayingTrack() != null) {
                body = init.getSongsAsJsonObject(currentlyPlaying, queue);
            }

        } else {
            body = new JSONObject().put("status", 404).put("message", "Derzeit spielt kein Lied.");
        }

        //TODO GUCKEN OB DOTENV ÜBERHAUPT VALUES HAT XD

        files.log(LogType.INFO, dotenv.get("HEADER_1_KEY"));
        files.log(LogType.INFO, dotenv.get("HEADER_2_KEY"));

        assert body != null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header(dotenv.get("HEADER_1_KEY"), dotenv.get("HEADER_1_VALUE"))
                .header(dotenv.get("HEADER_2_KEY"), dotenv.get("HEADER_2_VALUE"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static void sendAsyncUpdate(String action) throws ExecutionException, InterruptedException {
        GuildMusicManager msc = PlayerManager.getINSTANCE().getMusicManager();
        Main.executor.submit(() -> {
            if(apiIsAvailable()) {
                try {
                    sendUpdate(action, msc.scheduler.audioPlayer.getPlayingTrack(), msc.scheduler.queue);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static boolean apiIsAvailable(){
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host, port), 1000*5);
            errorSend = false;
            return true;
        }catch(IOException ex){
            if(!errorSend){
                files.log(LogType.WARNING, "API: Not available, check status.");
                errorSend = true;
            }
            return false;
        }
    }
}
