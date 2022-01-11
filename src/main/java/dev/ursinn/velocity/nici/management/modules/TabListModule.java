/*
 * MIT License
 *
 * Copyright (c) 2022 Ursin Filli
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.ursinn.velocity.nici.management.modules;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.player.TabListEntry;
import dev.ursinn.velocity.nici.management.ManagementPlugin;
import dev.ursinn.velocity.nici.management.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TabListModule {

    private final ProxyServer proxyServer;

    public TabListModule(ProxyServer proxyServer, ManagementPlugin plugin, Logger logger) {
        this.proxyServer = proxyServer;
        this.proxyServer.getScheduler().buildTask(plugin, this::update)
                .repeat(50L, TimeUnit.MILLISECONDS).schedule();

        logger.info("Loaded TabList Module.");
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
                serverConnection.ifPresent(connection -> player.getTabList().addEntry(TabListEntry.builder()
                        .displayName(Component.text()
                                .content("[")
                                .append(Utils.getServerName(connection.getServerInfo().getName()))
                                .append(Component.text("]"))
                                .append(Component.space())
                                .append(Component.text(others.getUsername()))
                                .build()
                        )
                        .latency((int) others.getPing())
                        .profile(others.getGameProfile())
                        .gameMode(3)
                        .tabList(player.getTabList())
                        .build()));
            }
        }
    }

    @Subscribe
    public void onChange(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        player.getTabList().clearHeaderAndFooter();
        Component header = Component.text()
                .content(" You are playing on ").color(NamedTextColor.AQUA)
                .append(Component.text("MC.NIKELS.CH ").color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("discord.gg/catland").color(NamedTextColor.GOLD))
                .build();
        Component footer = Component.text()
                .content("You are on the ").color(NamedTextColor.AQUA)
                .append(Utils.getServerName(player.getCurrentServer()).color(NamedTextColor.YELLOW))
                .append(Component.text(" Map").color(NamedTextColor.AQUA)).build();
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

}
