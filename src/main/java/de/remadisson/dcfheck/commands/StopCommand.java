package de.remadisson.dcfheck.commands;

import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.lavaplayer.PlayerManager;
import de.remadisson.dcfheck.manager.CInterface;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class StopCommand implements CInterface {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops and disconnects the Music-Bot.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if(!textChannel.getId().equalsIgnoreCase(Main.botChannelID)) return;

        System.out.println(event.getUser().getName() + " ("+event.getUser().getId()+") used: '" +event.getCommandString() + "'");

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("Du musst in einem Voice-Channel sein, um mich zu benutzten.").queue();
            return;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) return;

        if(!Objects.equals(event.getMember().getVoiceState().getChannel(), event.getGuild().getSelfMember().getVoiceState().getChannel()) && !event.getUser().getId().equals("268362677313601536")) return;

        PlayerManager.getINSTANCE().stopAndClearPlaying(textChannel.getGuild());

        event.reply("Musik-Widergabe wurde angehalten. (Queue ist leer)").queue();
    }
}
