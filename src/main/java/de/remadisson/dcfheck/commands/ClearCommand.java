package de.remadisson.dcfheck.commands;

import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClearCommand extends ListenerAdapter {

    String fheckomatorID = "763096551743553557";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if(!event.getAuthor().getId().equals("268362677313601536") && !Objects.requireNonNull(event.getMember()).getRoles().contains(event.getJDA().getRoleById(Long.parseLong(fheckomatorID)))) return;
        String[] message = event.getMessage().getContentDisplay().split(" ");
        String arg0 = message[0];
        String indicator = arg0.split("")[0];
        String command = arg0.substring(1);
        String[] args = Arrays.copyOfRange(message, 1, message.length);

        //if(!indicator.equals(main.botCommandIndicator) && !event.getAuthor().getId().equals(event.getGuild().getSelfMember().getId())){ event.getMessage().delete().queue(); return; }
        if(!command.equalsIgnoreCase("clear")) return;
        System.out.println(event.getAuthor().getName() + " ("+event.getAuthor().getId()+") used: '" + event.getMessage().getContentDisplay() + "'");


        System.out.println("Clearing ´"+textChannel.getName()+"´.");

        MessageHistory history = MessageHistory.getHistoryFromBeginning(textChannel).complete();
        List<Message> messages = history.getRetrievedHistory();
        textChannel.deleteMessages(messages).queue();
    }
}
