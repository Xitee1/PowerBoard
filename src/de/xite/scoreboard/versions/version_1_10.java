package de.xite.scoreboard.versions;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.ChatMessage;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter;

public class version_1_10 {
	public static Integer getPing(Player p) {
		return ((CraftPlayer) p).getHandle().ping;
	}
	@SuppressWarnings("deprecation")
	public static void sendTab(Player player, String head, String foot){
	    IChatBaseComponent header = new ChatMessage(head);
	    IChatBaseComponent footer = new ChatMessage(foot);
	    PacketPlayOutPlayerListHeaderFooter tablist = new PacketPlayOutPlayerListHeaderFooter();
	    try {
	    	Field headerField = tablist.getClass().getDeclaredField("a");
	        headerField.setAccessible(true);
	        headerField.set(tablist, header);
	        headerField.setAccessible(!headerField.isAccessible());
	        Field footerField = tablist.getClass().getDeclaredField("b");
	        footerField.setAccessible(true);
	        footerField.set(tablist, footer);
	        footerField.setAccessible(!footerField.isAccessible());
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    CraftPlayer cp = (CraftPlayer) player;
	    cp.getHandle().playerConnection.sendPacket(tablist);
	}
}
