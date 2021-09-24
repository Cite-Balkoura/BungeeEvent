package fr.milekat.grimtown.proxy.moderation.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;
import fr.milekat.grimtown.event.manager.EventManager;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Date;

public class Maintenance extends Command {
    public Maintenance() {
        super("maintenance", "mods.command.maintenance");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendHelp(sender);
            return;
        }
        Event event = MainBungee.getEvent();
        if (args[0].equalsIgnoreCase("off")) {
            event.setMaintenanceDate(event.getStartDate());
            EventManager.updateMaintenance(event);
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cMaintenance disable."));
            return;
        } else if (args[0].equalsIgnoreCase("on")) args[0] = "5m";
        long time = DateMileKat.parsePeriod(args[0]) + new Date().getTime();
        //  Check if value is less than 10s (10000ms)
        if (time < (new Date().getTime() + 10000)) {
            sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.core.messages.time_too_low")));
            return;
        }
        event.setMaintenanceDate(new Date(time));
        switchOn(event);
        EventManager.updateMaintenance(event);
        sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.core.messages.maintenance.enabled")));
    }

    /**
     * Kick all players if maintenance is enabled
     */
    private void switchOn(Event event) {
        if (event.isMaintenance()) {
            ProxyServer.getInstance().getPlayers().stream()
                    .filter(player -> player.hasPermission("mods.maintenance.bypass"))
                    .forEach(player -> player.disconnect(new TextComponent(
                            CoreUtils.getString("proxy.core.messages.maintenance.kick")
                    )));
        }
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX));
        sender.sendMessage(new TextComponent(
                "§6/maintenance <time>:§r Active la maintenance pour la durée indiquée et kick les joueurs."));
        sender.sendMessage(new TextComponent(
                "§6/maintenance on:§r Active la maintenance pour 5 minutes et kick les joueurs."));
        sender.sendMessage(new TextComponent("§6/maintenance off:§r Désactive la maintenance."));
    }
}
