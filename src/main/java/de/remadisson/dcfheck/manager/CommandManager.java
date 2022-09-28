package de.remadisson.dcfheck.manager;

import de.remadisson.dcfheck.files;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private List<CommandExecutor> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull ReadyEvent event){
        for(Guild guild : event.getJDA().getGuilds()){
            for(CommandExecutor command : commands){

                if(command.getOptionData() == null || command.getOptionData().isEmpty()){
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptionData()).queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        files.commandLog(event.getUser().getName() + " (" + event.getUser().getId() + ") used: '" + event.getCommandString() + "'");
        for(CommandExecutor command : commands){
            if(command.getName().equalsIgnoreCase(event.getName())){
                command.execute(event);
                return;
            }
        }
    }

    public void add(CommandExecutor command){
        commands.add(command);
    }
}
