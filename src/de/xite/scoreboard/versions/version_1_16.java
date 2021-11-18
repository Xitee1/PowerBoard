package de.xite.scoreboard.versions;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;

public class version_1_16 {
	static PowerBoard pl = PowerBoard.pl;
	public static Integer getPing(Player p) {
		return ((CraftPlayer) p).getHandle().ping;
	}
	public static void sendTab(Player p, String msg1, String msg2) {
	  PacketPlayOutPlayerListHeaderFooter packetPlayOutPlayerListHeaderFooter = new PacketPlayOutPlayerListHeaderFooter();
	  packetPlayOutPlayerListHeaderFooter.header = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+msg1+"\"}");
	  packetPlayOutPlayerListHeaderFooter.footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+msg2+"\"}");
	  ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutPlayerListHeaderFooter);
	}
}
