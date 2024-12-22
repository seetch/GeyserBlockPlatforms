package me.seetch.gbp.config;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Config {

    private Map<String, ServerConfig> servers;
    private List<String> exemptPlayers;
    private MessagesConfig messages;
    private Map<String, String> deviceNames = new HashMap<>();

    public boolean containsExemptPlayer(String username) {
        return exemptPlayers.contains(username);
    }

    @Getter
    @Setter
    public static class ServerConfig {
        private String mode;
        private List<String> platforms;

        public boolean equalsMode(String s) {
            return mode.equals(s);
        }

        public boolean containsPlatform(String s) {
            return platforms.contains(s);
        }
    }

    @Getter
    @Setter
    public static class MessagesConfig {
        private String blacklist;
        private String whitelist;
    }
}