package de.remadisson.dcfheck.event;

import de.remadisson.dcfheck.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.stream.Collectors;


public class AuditLogger extends ListenerAdapter {

    //Client ID remadisson : 268362677313601536
    //Client ID Maeggi : 291269711394635776
    //ChannelID Audit-Log : 1014932313005625477


    @Override
    public void onRoleUpdateColor(RoleUpdateColorEvent event){

        event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_UPDATE).limit(1).queue(list -> {
            if(list.isEmpty()) return;
            AuditLogEntry entry = list.get(0);

            Date date = new GregorianCalendar().getTime();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("RoleColorUpdate in group " + event.getRole().getName());
            eb.setDescription("at " + date.toLocaleString());
            eb.setColor(Color.CYAN);

            eb.addField("from", String.format("#%02x%02x%02x", event.getOldColor().getRed(), event.getOldColor().getGreen(), event.getOldColor().getBlue())  + "\n**to**\n" + String.format("#%02x%02x%02x", event.getNewColor().getRed(), event.getNewColor().getGreen(), event.getNewColor().getBlue()), false);

            eb.setAuthor(entry.getUser().getName(), null, entry.getUser().getAvatarUrl());
            Main.sendEmbedMessage(Objects.requireNonNull(event.getGuild().getTextChannelById("1014932313005625477")), eb);
        });
    }

    @Override
    public void onRoleUpdateName(RoleUpdateNameEvent event){

        event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_UPDATE).limit(1).queue(list -> {
            if(list.isEmpty()) return;
            AuditLogEntry entry = list.get(0);

            Date date = new GregorianCalendar().getTime();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("RoleUpdateName in group " + event.getRole().getName());
            eb.setDescription("at " + date.toLocaleString());
            eb.setColor(Color.CYAN);

            eb.addField("from", event.getOldName() + "\n**to**\n " + event.getNewName() +"", false);

            eb.setAuthor(entry.getUser().getName(), null, entry.getUser().getAvatarUrl());
            Main.sendEmbedMessage(Objects.requireNonNull(event.getGuild().getTextChannelById("1014932313005625477")), eb);
        });
    }

    @Override
    public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event){

        event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_UPDATE).limit(1).queue(list -> {
            if(list.isEmpty()) return;
            AuditLogEntry entry = list.get(0);

            Date date = new GregorianCalendar().getTime();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("RoleUpdatePermission in group " + event.getRole().getName());
            eb.setDescription("at " + date.toLocaleString());
            eb.setColor(Color.RED);

            ArrayList<String> newPermissions = new ArrayList<String>(event.getNewPermissions().stream().map(Permission::getName).toList());
            newPermissions.removeAll(event.getOldPermissions().stream().map(Permission::getName).toList());
            ArrayList<String> allPermissions = new ArrayList<String>(event.getOldPermissions().stream().map(Permission::getName).toList());
            allPermissions.removeAll(event.getNewPermissions().stream().map(Permission::getName).toList());
            allPermissions.addAll(newPermissions);


            eb.addField("The following permissions have changed:", "\n" + allPermissions.stream().map(pname -> {
                if(newPermissions.contains(pname)){
                    return "- **" + pname + "** from *false* to **true**";
                }
                return "- **" + pname + "** from *true* to***false**";
            }).collect(Collectors.joining("\n")), false);
            eb.setAuthor(entry.getUser().getName(), null, entry.getUser().getAvatarUrl());
            Main.sendEmbedMessage(Objects.requireNonNull(event.getGuild().getTextChannelById("1014932313005625477")), eb);
        });
    }


    @Override
    public void onGenericRole(GenericRoleEvent event) {
        if (event instanceof RoleCreateEvent) {
            event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_CREATE).limit(1).queue(list -> {
                if (list.isEmpty()) return;
                AuditLogEntry entry = list.get(0);

                Date date = new GregorianCalendar().getTime();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Role " + event.getRole().getName() + " has been **created**");
                eb.setDescription("at " + date.toLocaleString());
                eb.setColor(Color.GREEN);

                eb.setAuthor(entry.getUser().getName(), null, entry.getUser().getAvatarUrl());
                Main.sendEmbedMessage(Objects.requireNonNull(event.getGuild().getTextChannelById("1014932313005625477")), eb);
            });
        } else if(event instanceof RoleDeleteEvent){
            event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_DELETE).limit(1).queue(list -> {
                if (list.isEmpty()) return;
                AuditLogEntry entry = list.get(0);

                Date date = new GregorianCalendar().getTime();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Role " + event.getRole().getName() + " has been **deleted**");
                eb.setDescription("at " + date.toLocaleString());
                eb.setColor(Color.RED);

                eb.setAuthor(entry.getUser().getName(), null, entry.getUser().getAvatarUrl());
                Main.sendEmbedMessage(Objects.requireNonNull(event.getGuild().getTextChannelById("1014932313005625477")), eb);
            });
        }
    }

    /**
     * Das ist schon geheim hihi
     * @param event
     */
    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event){
        if(!Objects.equals(event.getUser().getId(), "291269711394635776") && !Objects.equals(event.getUser().getId(), "461923915791466497")) return;
        String message = event.getUser().getName() + "(" + event.getUser().getId() + ") is now " + event.getNewOnlineStatus().name();
        event.getJDA().getTextChannelById("1015204435691057202").sendMessage(message).queue();
        System.out.println(message);
    }
}
