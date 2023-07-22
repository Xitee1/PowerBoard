package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class version_1_10 extends VersionSpecific {

	private Field headerField;
	private Field footerField;

	@Override
	public void sendTab(Player p, String header, String footer) {
		try {
			Constructor<?> constructor = nmsClass("ChatMessage").getConstructor(String.class, Object[].class);
			Object head = constructor.newInstance(header, new Object[0]);
			Object foot = constructor.newInstance(footer, new Object[0]);

			send(p, head, foot);
		} catch (ReflectiveOperationException ex) {
			logger.severe("An error occurred while trying to set tab header/footer: " + ex.getMessage());
		}
	}

	protected void send(Player p, Object header, Object footer) throws ReflectiveOperationException {
		Object tablist = nmsClass("PacketPlayOutPlayerListHeaderFooter").getConstructor().newInstance();
		if (headerField == null) {
			headerField = tablist.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			footerField = tablist.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
		}

		headerField.set(tablist, header);
		footerField.set(tablist, footer);

		sendPacket(p, tablist);
	}
}