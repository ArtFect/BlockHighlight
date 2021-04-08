package ru.fiw.blockhighlight;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class PacketUtil {
    public static void sendPayload(Player receiver, String channel, ByteBuf bytes) {
        PacketContainer handle = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        handle.getMinecraftKeys().write(0, new MinecraftKey(channel));

        Object serializer = MinecraftReflection.getPacketDataSerializer(bytes);
        handle.getModifier().withType(ByteBuf.class).write(0, serializer);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, handle);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to send the packet", e);
        }
    }

    public static long blockPosToLong(int x, int y, int z) {
        return ((long) x & 67108863L) << 38 | (long) y & 4095L | ((long) z & 67108863L) << 12;
    }

    public static void d(ByteBuf packet, int i) {
        while ((i & -128) != 0) {
            packet.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        packet.writeByte(i);
    }

    public static void writeString(ByteBuf packet, String s) {
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
        d(packet, abyte.length);
        packet.writeBytes(abyte);
    }
}
