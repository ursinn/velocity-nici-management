package dev.ursinn.velocity.nici.management;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.ursinn.velocity.nici.management.modules.Message;
import dev.ursinn.velocity.nici.management.modules.PingList;
import dev.ursinn.velocity.nici.management.modules.TabList;
import org.slf4j.Logger;

@Plugin(
        id = "management",
        name = "Management",
        version = BuildConstants.VERSION,
        description = "Management Plugin",
        authors = {"ursinn"}
)
public class ManagementPlugin {

    private final ProxyServer proxyServer;
    private final Logger logger;

    @Inject
    public ManagementPlugin(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.proxyServer.getEventManager().register(this, new TabList(this.proxyServer, this, this.logger));
        this.proxyServer.getEventManager().register(this, new Message(this.proxyServer, this.logger));
        this.proxyServer.getEventManager().register(this, new PingList(this.proxyServer, this.logger));
    }
}
