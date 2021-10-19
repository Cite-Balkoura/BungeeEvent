package fr.milekat.grimtown.proxy.core.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Event;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.classes.Ban;
import fr.milekat.grimtown.proxy.moderation.managers.BanManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Date;

public class JoinHandler implements Listener {
    /**
     *
     */
    @EventHandler
    public void onTryJoin(LoginEvent event) {
        Profile profile = ProfileManager.getProfile(event.getConnection().getUniqueId());
        if (profile==null) {
            event.setCancelReason(new TextComponent(CoreUtils.getString("proxy.login.not_register")));
            event.setCancelled(true);
            return;
        }
        Event eventMc = MainBungee.getEvent();
        if (eventMc.getStartDate().getTime() > new Date().getTime()) {
            if (profile.nonStaff()) {
                event.setCancelReason(new TextComponent(CoreUtils.getString("proxy.login.not_started")));
                event.setCancelled(true);
                return;
            }
        }
        if (eventMc.getMaintenanceDate().getTime() > new Date().getTime()) {
            if (profile.nonStaff()) {
                event.setCancelReason(new TextComponent(CoreUtils.getString("proxy.login.maintenance")));
                event.setCancelled(true);
                return;
            }
        }
        if (profile.nonStaff() && getNonStaff().size() >= MainBungee.getConfig().getInt("proxy.core.max_players")) {
            event.setCancelReason(new TextComponent(CoreUtils.getString("proxy.login.full")));
            event.setCancelled(true);
            return;
        }
        if (BanManager.isBanned(profile)) {
            Ban ban = BanManager.getLastBan(profile);
            event.setCancelReason(new TextComponent(CoreUtils.getString("proxy.login.ban", ban)));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoinUpdateProfile(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), () ->
                ProfileManager.updateUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName())
        );
    }

    /**
     *
     */
    private ArrayList<ProxiedPlayer> getNonStaff() {
        ArrayList<ProxiedPlayer> online = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.hasPermission("mods")) online.add(player);
        }
        return online;
    }
}
