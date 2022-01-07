package dev.ursinn.velocity.nici.management.modules;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class Message {

    private final ProxyServer proxyServer;

    public Message(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        logger.info("Loaded Message.");
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        broadcast(player, message);
    }

    private void broadcast(Player player, String message) {
        Optional<ServerConnection> serverConnection = player.getCurrentServer();
        RegisteredServer registeredServer = null;
        if (serverConnection.isPresent()) {
            registeredServer = serverConnection.get().getServer();
        }

        String sendMessage = serverConnection.map(connection -> "[" + connection.getServerInfo().getName() + "] <" + player.getUsername() + "> " + message)
                .orElseGet(() -> "[UNKNOWN] <" + player.getUsername() + "> " + message);

        TextComponent textComponent = Component.text(sendMessage);
        // send message to other server
        for (RegisteredServer server : this.proxyServer.getAllServers()) {
            if (!Objects.equals(server, registeredServer)) {
                server.sendMessage(textComponent);
            }
        }
    }

}
