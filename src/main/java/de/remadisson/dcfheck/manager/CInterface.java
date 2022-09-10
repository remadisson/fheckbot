package de.remadisson.dcfheck.manager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface CInterface {
    String getName();
    String getDescription();
    List<OptionData> getOptionData();
    void execute(SlashCommandInteractionEvent event);

}
