package me.seetch.gbp.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.util.DeviceOs;

import java.util.UUID;

public class Util {

    private static final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').hexCharacter('#').hexColors().build();

    public static boolean isBedrockPlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }

    public static DeviceOs getBedrockPlatform(UUID uuid) {
        FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
        if (player != null)
            return getProperDeviceOs(player.getDeviceOs());
        return null;
    }

    public static DeviceOs getProperDeviceOs(DeviceOs deviceOS) {
        if (deviceOS == DeviceOs.NX)
            return DeviceOs.PS4;
        return deviceOS;
    }

    public static TextComponent color(String s) {
        return serializer.deserialize(s);
    }
}
