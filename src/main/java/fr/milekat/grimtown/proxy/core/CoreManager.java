package fr.milekat.grimtown.proxy.core;

import fr.milekat.grimtown.proxy.core.events.JoinHandler;
import fr.milekat.grimtown.proxy.core.events.ProxyPing;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class CoreManager {
    public CoreManager(Plugin plugin, PluginManager pm) {
        pm.registerListener(plugin, new JoinHandler());
        pm.registerListener(plugin, new ProxyPing());
    }
}
