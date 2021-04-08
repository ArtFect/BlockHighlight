package ru.fiw.blockhighlight;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {
    public static void sendStopToAll() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            sendStop(pl);
        }
    }

    public static void sendHideBehindBlocks(Player pl, int time) {
        sendBlockHighlight(pl, BlockHighlight.getHideBehindBlocks(time));
    }

    public static void sendHideBehindBlocksAlways(Player pl) {
        sendHideBehindBlocks(pl, 1000000000);
    }

    public static void sendHideBehindBlocksAlwaysToAll() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            Util.sendHideBehindBlocksAlways(pl);
        }
    }

    public static void sendBlockHighlight(Player pl, BlockHighlight highlight) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeLong(PacketUtil.blockPosToLong(highlight.getX(), highlight.getY(), highlight.getZ()));
        packet.writeInt(highlight.getColor());

        String text = ChatColor.translateAlternateColorCodes('&', highlight.getText());
        if (Main.placeholderApiLoaded) {
            PacketUtil.writeString(packet, PlaceholderAPI.setPlaceholders(pl, text));
        } else {
            PacketUtil.writeString(packet, text);
        }
        packet.writeInt(highlight.getTime());

        PacketUtil.sendPayload(pl, "debug/game_test_add_marker", packet);
    }

    public static void sendStop(Player pl) {
        PacketUtil.sendPayload(pl, "debug/game_test_clear", Unpooled.wrappedBuffer(new byte[0]));
    }
}
