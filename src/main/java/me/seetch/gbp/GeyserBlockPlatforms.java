package me.seetch.gbp;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.seetch.gbp.command.ReloadCommand;
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
        server.getCommandManager().register("gbpreload", new ReloadCommand(this));
    }

    public void loadConfig() {
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

        defaultConfig.setServers(Map.of(
                "lobby", createServerConfig("blacklist", List.of("Java", "Unknown")),
                "server-1", createServerConfig("blacklist", List.of("Java", "Unknown")),
                "server-2", createServerConfig("whitelist", List.of("Android", "iOS"))
        ));

        defaultConfig.setExemptPlayers(List.of("seetch"));

        defaultConfig.setMessages(new Config.MessagesConfig());
        defaultConfig.getMessages().setBlacklist("&cYou can't connect from &f%platforms%");
        defaultConfig.getMessages().setWhitelist("&aYou can connect only from &f%platforms%");

        defaultConfig.setDeviceNames(new HashMap<>() {{
            put("Java", "Java");
            put("Unknown", "Unknown");
            put("Android", "Android");
            put("iOS", "iOS");
            put("macOS", "macOS");
            put("Amazon", "Amazon");
            put("Gear VR", "Gear VR");
            put("Hololens", "Hololens");
            put("Windows", "Windows");
            put("Windows x86", "Windows x86");
            put("Dedicated", "Dedicated");
            put("Apple TV", "Apple TV");
            put("PS4", "PS4");
            put("Switch", "Switch");
            put("Xbox One", "Xbox One");
            put("Windows Phone", "Windows Phone");
        }});

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer();
        representer.addClassTag(Config.class, org.yaml.snakeyaml.nodes.Tag.MAP);

        Yaml yaml = new Yaml(representer, options);

        String comments = "# Use 'Java' to regulate connect from Java Edition.\n\n";

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