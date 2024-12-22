package me.seetch.gbp;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.seetch.gbp.config.Config;
import me.seetch.gbp.handler.EventHandler;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Plugin(id = "geyserblockplatforms", name = "GeyserBlockPlatforms", version = BuildConstants.VERSION, authors = {"seetch"})
public class GeyserBlockPlatforms {

    @Inject
    private ProxyServer server;
    @Inject
    private Logger logger;

    @Getter
    private Config config;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        loadConfig();
        server.getEventManager().register(this, new EventHandler(this));
    }

    private void loadConfig() {
        Path configPath = Paths.get("plugins", "GeyserBlockPlatforms", "config.yml");
        File configFile = configPath.toFile();

        if (!configFile.exists()) {
            try {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
                saveDefaultConfig(configFile);
            } catch (IOException e) {
                logger.error("Could not create configuration file", e);
                return;
            }
        }

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            config = yaml.load(inputStream);
        } catch (IOException e) {
            logger.error("Could not load configuration file", e);
        }
    }

    private void saveDefaultConfig(File configFile) {
        Config defaultConfig = new Config();

        // Создаем серверные конфигурации
        defaultConfig.setServers(Map.of(
                "lobby", createServerConfig("blacklist", List.of("JAVA", "UNKNOWN")),
                "server-1", createServerConfig("blacklist", List.of("JAVA", "UNKNOWN")),
                "server-2", createServerConfig("whitelist", List.of("GOOGLE", "IOS"))
        ));

        // Игнорируемые игроки
        defaultConfig.setExemptPlayers(List.of("seetch"));

        // Сообщения
        defaultConfig.setMessages(new Config.MessagesConfig());
        defaultConfig.getMessages().setBlacklist("&cYou can't connect from &f%platforms%");
        defaultConfig.getMessages().setWhitelist("&aYou can connect only from &f%platforms%");

        defaultConfig.setDeviceNames(new HashMap<>() {{
            put("JAVA", "Java");
            put("UNKNOWN", "Unknown");
            put("GOOGLE", "Android");
            put("IOS", "iOS");
            put("OSX", "macOS");
            put("AMAZON", "Amazon");
            put("GEARVR", "Gear VR");
            put("HOLOLENS", "Hololens");
            put("UWP", "Windows");
            put("WIN32", "Windows x86");
            put("DEDICATED", "Dedicated");
            put("TVOS", "Apple TV");
            put("PS4", "PS4");
            put("NX", "Switch");
            put("XBOX", "Xbox One");
            put("WINDOWS_PHONE", "Windows Phone");
        }});

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer();
        representer.addClassTag(Config.class, org.yaml.snakeyaml.nodes.Tag.MAP);

        Yaml yaml = new Yaml(representer, options);

        String comments = "# You can view the list of devices at the link (you must use a key):\n" +
                "# https://github.com/GeyserMC/Geyser/blob/master/common/src/main/java/org/geysermc/floodgate/util/DeviceOs.java\n" +
                "# Use 'JAVA' to regulate connect from Java Edition.\n\n";

        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(comments);
            yaml.dump(defaultConfig, writer);
        } catch (IOException e) {
            logger.error("Could not save default configuration file", e);
        }
    }

    private Config.ServerConfig createServerConfig(String mode, List<String> platforms) {
        Config.ServerConfig serverConfig = new Config.ServerConfig();
        serverConfig.setMode(mode);
        serverConfig.setPlatforms(platforms);
        return serverConfig;
    }
}