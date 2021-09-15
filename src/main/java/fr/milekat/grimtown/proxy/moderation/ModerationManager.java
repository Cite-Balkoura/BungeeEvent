package fr.milekat.grimtown.proxy.moderation;

import fr.milekat.grimtown.proxy.moderation.commands.*;
import fr.milekat.grimtown.proxy.moderation.events.BanDiscord;
import fr.milekat.grimtown.proxy.moderation.events.MuteDiscord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class ModerationManager {
    public ModerationManager(Plugin plugin, PluginManager pm) {
        //  Mute
        pm.registerListener(plugin, new MuteDiscord());
        pm.registerCommand(plugin, new MuteCmd());
        pm.registerCommand(plugin, new UnMute());
        //  Ban
        pm.registerListener(plugin, new BanDiscord());
        pm.registerCommand(plugin, new BanCmd());
        pm.registerCommand(plugin, new UnBan());
        //  Kick
        pm.registerCommand(plugin, new Kick());
        //  Maintenance
        pm.registerCommand(plugin, new Maintenance());
    }
}
