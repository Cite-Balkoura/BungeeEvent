package fr.milekat.grimtown.proxy;

import fr.milekat.grimtown.proxy.core.CoreManager;
import fr.milekat.grimtown.proxy.moderation.ModerationManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class ProxyManager {
    public ProxyManager(Plugin plugin, PluginManager pm) {
        new CoreManager(plugin, pm);
        new ModerationManager(plugin, pm);
    }
}
