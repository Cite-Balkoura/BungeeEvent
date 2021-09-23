package fr.milekat.grimtown.proxy.core.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.classes.Ban;
import fr.milekat.grimtown.proxy.moderation.managers.BanManager;
import fr.milekat.utils.DateMileKat;
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
        if (ProfileManager.notExists(event.getConnection().getUniqueId())) {
            event.setCancelReason(new TextComponent(getNode("proxy.login.not_register")));
            event.setCancelled(true);
            return;
        }
        Profile profile = ProfileManager.getProfile(event.getConnection().getUniqueId());
        Event eventMc = MainBungee.getEvent();
        if (eventMc.getStartDate().getTime() > new Date().getTime()) {
            if (!profile.isStaff()) {
                event.setCancelReason(new TextComponent(getNode("proxy.login.not_started")));
                event.setCancelled(true);
                return;
            }
        }
        if (eventMc.getMaintenanceDate().getTime() > new Date().getTime()) {
            if (!profile.isStaff()) {
                event.setCancelReason(new TextComponent(getNode("proxy.login.maintenance")));
                event.setCancelled(true);
                return;
            }
        }
        if (getNonStaff().size() >= 150) {
            event.setCancelReason(new TextComponent(MainBungee.getConfig().getString("proxy.login.full")));
            event.setCancelled(true);
            return;
        }
        if (BanManager.isBanned(profile)) {
            Ban ban = BanManager.getLastBan(profile);
            event.setCancelReason(new TextComponent(MainBungee.getConfig().getString("proxy.login.ban")
                    .replaceAll("<BAN_TIME>", DateMileKat.reamingToString(ban.getBanDate()))
                    .replaceAll("<REASON>", ban.getReasonBan())));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoinUpdateProfile(PostLoginEvent event) {
        ProfileManager.updateUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    /*  Send player to his last server
    @EventHandler
    public void onJoinEvent(ServerConnectEvent event) {
        if (!event.getTarget().getName().equalsIgnoreCase("event")) return;
        if (event.getPlayer().hasPermission("mods.event.connect.bypass")) return;
        try {
            Connection connection = MainBungee.getSql();
            PreparedStatement q = connection.prepareStatement("SELECT `value` FROM `mcpg_config` WHERE `name` = ?;");
            q.setString(1, "EVENT");
            q.execute();
            q.getResultSet().next();
            if (!q.getResultSet().getBoolean("value")) {
                if (event.getPlayer().getServer() != null) {
                    event.setCancelled(true);
                } else {
                    event.setTarget(ProxyServer.getInstance().getServerInfo("cite"));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
     */

    /**
     *
     */
    private String getNode(String path) {
        Event event = MainBungee.getEvent();
        return MainBungee.getConfig().getString(path).replaceAll("<PREFIX>", MainBungee.PREFIX)
                .replaceAll("<EVENT_TIME>", DateMileKat.reamingToString(event.getStartDate()))
                .replaceAll("<MAINTENANCE_TIME>", DateMileKat.reamingToString(event.getMaintenanceDate()));
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
