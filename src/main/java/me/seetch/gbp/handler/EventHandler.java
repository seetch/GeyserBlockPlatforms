package me.seetch.gbp.handler;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.seetch.gbp.GeyserBlockPlatforms;
import me.seetch.gbp.config.Config;
import me.seetch.gbp.util.Mode;
import me.seetch.gbp.util.Util;
import net.kyori.adventure.text.TextComponent;
import org.geysermc.floodgate.util.DeviceOs;

import java.util.Map;

public class EventHandler {

    private final Config config;

    public EventHandler(GeyserBlockPlatforms plugin) {
        config = plugin.getConfig();
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();

        RegisteredServer previousServer = event.getPreviousServer();
        RegisteredServer targetServer = event.getOriginalServer();

        if (config.containsExemptPlayer(player.getUsername())) {
            return;
        }

        DeviceOs os;
        if (Util.isBedrockPlayer(player.getUniqueId())) {
            os = Util.getBedrockPlatform(player.getUniqueId());
        } else {
            os = null;
        }

        for (Map.Entry<String, Config.ServerConfig> entry : config.getServers().entrySet()) {
            String serverName = entry.getKey();
            Config.ServerConfig serverConfig = entry.getValue();

            if (targetServer.getServerInfo().getName().equals(serverName)) {
                String platform = (os != null) ? os.toString() : "JAVA";

                Mode modeKey = serverConfig.equalsMode("whitelist") ? Mode.WHITELIST :
                        (serverConfig.equalsMode("blacklist") ? Mode.BLACKLIST : null);

                if (modeKey != null) {
                    boolean isWhitelist = modeKey.equals(Mode.WHITELIST);
                    boolean isPlatformMatched = serverConfig.containsPlatform(platform);

                    if ((isWhitelist && !isPlatformMatched) || (isPlatformMatched && !isWhitelist)) {
                        String formattedPlatformsStr = String.join(", ",
                                serverConfig.getPlatforms().stream()
                                        .map(p -> config.getDeviceNames().getOrDefault(p, p))
                                        .toArray(String[]::new));

                        TextComponent message = Util.color(
                                serverConfig.equalsMode("whitelist") ?
                                        config.getMessages().getWhitelist().replace("%platforms%", formattedPlatformsStr) :
                                        config.getMessages().getBlacklist().replace("%platforms%", formattedPlatformsStr)
                        );

                        if (previousServer == null) {
                            player.disconnect(message);
                        } else {
                            event.setResult(ServerPreConnectEvent.ServerResult.denied());
                            player.sendMessage(message);
                        }
                        return;
                    }
                }
            }
        }
    }
}
