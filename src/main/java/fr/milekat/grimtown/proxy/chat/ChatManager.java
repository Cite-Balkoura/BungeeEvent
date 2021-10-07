package fr.milekat.grimtown.proxy.chat;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.proxy.chat.commands.*;
import fr.milekat.grimtown.proxy.chat.engine.Announcement;
import fr.milekat.grimtown.proxy.chat.engine.Spam;
import fr.milekat.grimtown.proxy.chat.events.Chat;
import fr.milekat.grimtown.proxy.chat.events.Connect;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatManager {
    private static final ArrayList<ProxiedPlayer> CHAT_MODS = new ArrayList<>();
    public ChatManager(Plugin plugin, PluginManager pm) {
        HashMap<UUID, String> msg_last = new HashMap<>();
        HashMap<UUID, Integer> msg_recent = new HashMap<>();
        HashMap<ProxiedPlayer, ProxiedPlayer> private_last = new HashMap<>();
        HashMap<ProxiedPlayer, Team> chat_team = new HashMap<>();
        pm.registerListener(plugin, new Connect(msg_last, msg_recent));
        pm.registerListener(plugin, new Chat(msg_last, msg_recent, chat_team));
        new Announcement(10L);
        new Spam(msg_recent, 500L);
        pm.registerCommand(plugin, new PrivateMessage(private_last));
        pm.registerCommand(plugin, new Reply(private_last));
        pm.registerCommand(plugin, new Announce());
        pm.registerCommand(plugin, new ChatMode(chat_team));
        pm.registerCommand(plugin, new RemoveMessage());
        if (MainBungee.getEvent().getEventFeatures().contains(Event.EventFeature.TEAM)) pm.registerCommand(plugin, new ChatTeam());
    }

    public static ArrayList<ProxiedPlayer> getMods() {
        return CHAT_MODS;
    }
}
