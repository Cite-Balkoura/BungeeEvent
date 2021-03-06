package fr.milekat.grimtown.proxy.core.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ProxyPing implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();
        if (new Date().before(MainBungee.getEvent().getStartDate())) {
            ping.setDescriptionComponent(new TextComponent(CoreUtils.getString("proxy.motd.before.motd")));
            ping.getVersion().setProtocol(MainBungee.getConfig().getInt("proxy.motd.before.protocol"));
            ping.getVersion().setName(CoreUtils.getString("proxy.motd.before.ping_msg"));
            ArrayList<ServerPing.PlayerInfo> samples = new ArrayList<>();
            Arrays.stream(CoreUtils.getString("proxy.motd.before.ping_hover").split("\\n")).forEach(s ->
                    samples.add(new ServerPing.PlayerInfo(s.replaceAll("<EVENT_TIME>",
                            DateMileKat.reamingToString(MainBungee.getEvent().getStartDate())), ""))
            );
            ping.getPlayers().setSample(samples.toArray(new ServerPing.PlayerInfo[0]));
        } else if (new Date().before(MainBungee.getEvent().getMaintenanceDate())) {
            ping.setDescriptionComponent(new TextComponent(CoreUtils.getString("proxy.motd.maintenance.motd")));
            ping.getVersion().setProtocol(MainBungee.getConfig().getInt("proxy.motd.maintenance.protocol"));
            ping.getVersion().setName(CoreUtils.getString("proxy.motd.maintenance.ping_msg"));
            ArrayList<ServerPing.PlayerInfo> samples = new ArrayList<>();
            Arrays.stream(CoreUtils.getString("proxy.motd.maintenance.ping_hover").split("\\n")).forEach(s ->
                    samples.add(new ServerPing.PlayerInfo(s.replaceAll("<MAINTENANCE_TIME>",
                            DateMileKat.reamingToString(MainBungee.getEvent().getStartDate())), ""))
            );
            ping.getPlayers().setSample(samples.toArray(new ServerPing.PlayerInfo[0]));
        } else {
            ping.setDescriptionComponent(new TextComponent(CoreUtils.getString("proxy.motd.open.motd")));
            ArrayList<ServerPing.PlayerInfo> online = new ArrayList<>();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (!player.hasPermission("mods")) online.add(new ServerPing.PlayerInfo(player.getName(), ""));
            }
            ping.getVersion().setName(CoreUtils.getString("proxy.motd.open.ping_msg"));
            ArrayList<ServerPing.PlayerInfo> samples = new ArrayList<>();
            Arrays.stream(CoreUtils.getString("proxy.motd.open.ping_hover").split("\\n")).forEach(s ->
                    samples.add(new ServerPing.PlayerInfo(s, ""))
            );
            ping.setPlayers(new ServerPing.Players(150, online.size(), samples.toArray(new ServerPing.PlayerInfo[0])));
        }
        event.setResponse(ping);
    }
}
