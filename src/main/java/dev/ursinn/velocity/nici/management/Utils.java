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

package dev.ursinn.velocity.nici.management;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class Utils {

    private Utils() {}

    public static @NotNull Component getServerName(String serverName) {
        return Component.text(Character.toUpperCase(serverName.charAt(0)) + serverName.substring(1).replace("_", "."));
    }

    public static @NotNull Component getServerName(Optional<ServerConnection> serverConnection) {
        return serverConnection
                .map(connection -> getServerName(connection.getServerInfo().getName()))
                .orElse(Component.text("UNKNOWN"));
    }

    public static @NotNull Component getPrefix(Player player) {
        if (player.hasPermission("nici.management.admin")) {
            return Component.text()
                    .content("[Admin] ").color(NamedTextColor.RED)
                    .append(Component.text(player.getUsername()).color(NamedTextColor.GOLD))
                    .build();
        }

        return Component.text(player.getUsername()).color(NamedTextColor.GREEN);
    }

    public static void broadcast(ProxyServer proxyServer, Player player, Component message) {
        Optional<ServerConnection> serverConnection = player.getCurrentServer();
        RegisteredServer registeredServer = null;
        if (serverConnection.isPresent()) {
            registeredServer = serverConnection.get().getServer();
        }

        Component component = Component.text()
                .content("")
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(Utils.getServerName(serverConnection).color(NamedTextColor.YELLOW))
                .append(Component.text("] ").color(NamedTextColor.GRAY))
                .append(Utils.getPrefix(player))
                .append(Component.text(":"))
                .append(Component.space())
                .append(message)
                .build();

        // send message to other server
        for (RegisteredServer server : proxyServer.getAllServers()) {
            if (!Objects.equals(server, registeredServer)) {
                server.sendMessage(component);
            }
        }
    }

}
