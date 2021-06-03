package de.xite.scoreboard.versions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class version_1_08 {
	public static Integer getPing(Player p) {
		return ((CraftPlayer) p).getHandle().ping;
	}
	
	public static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	}
	public static String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}
	public static void sendTab(Player p, String msg1, String msg2) {
		try {
			if ((getServerVersion().equalsIgnoreCase("v1_9_R1")) || (getServerVersion().equalsIgnoreCase("v1_9_R2"))) {
				Object header = getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { msg1 });
				Object footer = getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { msg2 });
        
				Object ppoplhf = getNmsClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNmsClass("IChatBaseComponent") }).newInstance(new Object[] { header });
        
				Field f = ppoplhf.getClass().getDeclaredField("b");
				f.setAccessible(true);
			  	f.set(ppoplhf, footer);
        
			  	Object nmsp = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
			  	Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);
        
			  	pcon.getClass().getMethod("sendPacket", new Class[] { getNmsClass("Packet") }).invoke(pcon, new Object[] { ppoplhf });
			} else if ((getServerVersion().equalsIgnoreCase("v1_8_R2")) || (getServerVersion().equalsIgnoreCase("v1_8_R3"))) {
				Object header = getNmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{'text': '" + msg1 + "'}" });
				Object footer = getNmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{'text': '" + msg2 + "'}" });
        
				Object ppoplhf = getNmsClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNmsClass("IChatBaseComponent") }).newInstance(new Object[] { header });
        
				Field f = ppoplhf.getClass().getDeclaredField("b");
				f.setAccessible(true);
				f.set(ppoplhf, footer);
        
				Object nmsp = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
				Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);
        	
				pcon.getClass().getMethod("sendPacket", new Class[] { getNmsClass("Packet") }).invoke(pcon, new Object[] { ppoplhf });
			} else {
				Object header = getNmsClass("ChatSerializer").getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{'text': '" + msg1 + "'}" });
				Object footer = getNmsClass("ChatSerializer").getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{'text': '" + msg2 + "'}" });
        
				Object ppoplhf = getNmsClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNmsClass("IChatBaseComponent") }).newInstance(new Object[] { header });
        
				Field f = ppoplhf.getClass().getDeclaredField("b");
				f.setAccessible(true);
				f.set(ppoplhf, footer);
        
				Object nmsp = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
				Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);
        
				pcon.getClass().getMethod("sendPacket", new Class[] { getNmsClass("Packet") }).invoke(pcon, new Object[] { ppoplhf });
			}
		} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException|ClassNotFoundException|InstantiationException|NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
