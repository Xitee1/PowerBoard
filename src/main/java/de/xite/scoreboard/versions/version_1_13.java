package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class version_1_13 extends VersionSpecific {

	@Override
	public void sendTab(Player p, String header, String footer) {
		try {
			Class<?> packetClass = nmsClass("PacketPlayOutPlayerListHeaderFooter");
			Object packet = packetClass.getConstructor().newInstance();
			Method serialize = nmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class);

			packetClass.getField("header").set(packet, serialize.invoke(null, "{\"text\": \"" + header + "\"}"));
			packetClass.getField("footer").set(packet, serialize.invoke(null, "{\"text\": \"" + footer + "\"}"));

			sendPacket(p, packet);
		} catch (ReflectiveOperationException ex) {
			logger.severe("An error occurred while trying to set tab header/footer: " + ex.getMessage());
		}
	}
}