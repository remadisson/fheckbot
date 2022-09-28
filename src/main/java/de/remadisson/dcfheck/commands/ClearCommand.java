package de.remadisson.dcfheck.commands;

import de.remadisson.dcfheck.Main;
import de.remadisson.dcfheck.files;
import de.remadisson.dcfheck.manager.CommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ClearCommand implements CommandExecutor {

    String fheckomatorID = "763096551743553557";

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears all messages from a Channel! BE CAREFUL!";
    }

    @Override
    public List<OptionData> getOptionData() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals("268362677313601536") && !Objects.requireNonNull(event.getMember()).getRoles().contains(event.getJDA().getRoleById(Long.parseLong(fheckomatorID)))) return;


        if (event.getChannel().getName().toLowerCase().contains("log")){
            event.reply("Du kannst diesen Channel nicht Clearen!").queue(msg -> {
                 msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
            });
            return;
        }
        files.commandLog("Clearing ´" + event.getMessageChannel().getName() + "´.");
        event.getChannel().asTextChannel().createCopy().queue(textChannel -> {
            if(textChannel.getName().equalsIgnoreCase("bot-commands")){
                Main.botChannelID = textChannel.getId();
            }
        });
        event.getChannel().delete().queue();
    }
}
