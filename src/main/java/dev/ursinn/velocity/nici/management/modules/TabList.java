package dev.ursinn.velocity.nici.management.modules;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.player.TabListEntry;
import dev.ursinn.velocity.nici.management.ManagementPlugin;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TabList {

    private final ProxyServer proxyServer;

    public TabList(ProxyServer proxyServer, ManagementPlugin plugin, Logger logger) {
        this.proxyServer = proxyServer;
        this.proxyServer.getScheduler().buildTask(plugin, this::update)
                .repeat(50L, TimeUnit.MILLISECONDS).schedule();

        logger.info("Loaded TabList.");
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        for (Player others : this.proxyServer.getAllPlayers()) {
            if (others.getTabList().containsEntry(player.getUniqueId())) {
                others.getTabList().removeEntry(player.getUniqueId());
            }
        }
    }

    private void update() {
        for (Player player : this.proxyServer.getAllPlayers()) {
            for (Player others : this.proxyServer.getAllPlayers()) {
                if (player.getTabList().containsEntry(others.getUniqueId()) || player.getUniqueId().equals(others.getUniqueId())) {
                    continue;
                }

                Optional<ServerConnection> serverConnection = others.getCurrentServer();
                if (serverConnection.isPresent()) {
                    String serverName = serverConnection.get().getServerInfo().getName();
                    player.getTabList().addEntry(TabListEntry.builder()
                            .displayName(Component.text("[" + serverName + "] " + others.getUsername()))
                            .latency((int) others.getPing())
                            .profile(others.getGameProfile())
                            .gameMode(3)
                            .tabList(player.getTabList())
                            .build());
                }
            }
        }
    }

}