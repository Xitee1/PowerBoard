package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class version_1_08 extends VersionSpecific {

	private final boolean newerMethod = (NMS_VERSION.equals("v1_8_R2")) || (NMS_VERSION.equals("v1_8_R3"));

	public void sendTab(Player p, String header, String footer) {
		try {
			Method a = newerMethod ?
					nmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class) :
					nmsClass("ChatSerializer").getMethod("a", String.class); // 1.8.0

			send(p, a.invoke(null, "{'text': '" + header + "'}"), a.invoke(null, "{'text': '" + footer + "'}"));
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private void send(Player p, Object header, Object footer) throws ReflectiveOperationException {
		Object packet = nmsClass("PacketPlayOutPlayerListHeaderFooter")
				.getConstructor(nmsClass("IChatBaseComponent")).newInstance(header);
		Field f = packet.getClass().getDeclaredField("b");
		f.setAccessible(true);
		f.set(packet, footer);
		sendPacket(p, packet);
	}
}