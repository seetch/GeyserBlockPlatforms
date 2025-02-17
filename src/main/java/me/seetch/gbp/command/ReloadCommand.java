package me.seetch.gbp.command;

import com.velocitypowered.api.command.SimpleCommand;
import me.seetch.gbp.GeyserBlockPlatforms;
import net.kyori.adventure.text.Component;

public class ReloadCommand implements SimpleCommand {

    private final GeyserBlockPlatforms plugin;

    public ReloadCommand(GeyserBlockPlatforms plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        plugin.loadConfig();
        invocation.source().sendMessage(Component.text("Configuration reloaded!"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return (invocation.source()).hasPermission("geyserblockplatforms.reload");
    }
}
