package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class version_1_12 extends version_1_10 {

	@Override
	public void sendTab(Player p, String header, String footer) {
		try {
			Method serialize = nmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
			Object head = serialize.invoke(null, "{\"text\": \"" + header + "\"}");
			Object foot = serialize.invoke(null, "{\"text\": \"" + footer + "\"}");

			send(p, head, foot);
		} catch (ReflectiveOperationException ex) {
			logger.severe("An error occurred while trying to set tab header/footer: " + ex.getMessage());
		}
	}
}