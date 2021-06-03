package de.xite.scoreboard.versions;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;

public class version_1_12 {
	public static Integer getPing(Player p) {
		return ((CraftPlayer) p).getHandle().ping;
	}
	public static void sendTab(Player p, String header, String footer) {
         IChatBaseComponent tabheader = ChatSerializer.a("{\"text\": \"" + header + "\"}");
         IChatBaseComponent tabfooter = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
         PacketPlayOutPlayerListHeaderFooter tablist = new PacketPlayOutPlayerListHeaderFooter();
         try {
             Field headerField = tablist.getClass().getDeclaredField("a");
             headerField.setAccessible(true);
             headerField.set(tablist, tabheader);
             headerField.setAccessible(!headerField.isAccessible());
             Field footerField = tablist.getClass().getDeclaredField("b");
             footerField.setAccessible(true);
             footerField.set(tablist, tabfooter);
             footerField.setAccessible(!footerField.isAccessible());
         } catch (Exception var11) {
             var11.printStackTrace();
         } finally {
             ((CraftPlayer)p).getHandle().playerConnection.sendPacket(tablist);
         }
     }
}
