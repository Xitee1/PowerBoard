package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class version_1_09 extends VersionSpecific {

	public void sendTab(Player p, String header, String footer) {
		try {
			Constructor<?> constructor = nmsClass("ChatComponentText").getConstructor(String.class);
			Object head = constructor.newInstance(header);
			Object foot = constructor.newInstance(footer);

			Object packet = nmsClass("PacketPlayOutPlayerListHeaderFooter")
					.getConstructor(nmsClass("IChatBaseComponent")).newInstance(head);
			Field footerfield = packet.getClass().getDeclaredField("b");
			footerfield.setAccessible(true);
			footerfield.set(packet, foot);

			sendPacket(p, packet);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}
}