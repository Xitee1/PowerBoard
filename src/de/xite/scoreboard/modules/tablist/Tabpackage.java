package de.xite.scoreboard.modules.tablist;

import java.util.ConcurrentModificationException;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Version;
import de.xite.scoreboard.versions.version_1_08;
import de.xite.scoreboard.versions.version_1_10;
import de.xite.scoreboard.versions.version_1_11;
import de.xite.scoreboard.versions.version_1_12;
import de.xite.scoreboard.versions.version_1_13;
import de.xite.scoreboard.versions.version_1_14;
import de.xite.scoreboard.versions.version_1_15;
import de.xite.scoreboard.versions.version_1_16;
import de.xite.scoreboard.versions.version_1_17;

public class Tabpackage {
	static PowerBoard pl = PowerBoard.pl;

	public static void send(Player p) {
		String header = "", footer = "";
		
		
		if(TabManager.headers.isEmpty() || TabManager.footers.isEmpty())
			return;
		
		// feel free to implement a better solution. I currently can't find a better way of avoiding this ConcurrentModificationException
		try {
			if(!TabManager.currentHeader.containsKey(p) || !TabManager.currentFooter.containsKey(p))
				return;
			for(Entry<Integer, String> e : TabManager.currentHeader.get(p).entrySet())
				header += e.getValue()+"\n";
			for(Entry<Integer, String> e : TabManager.currentFooter.get(p).entrySet())
				footer += e.getValue()+"\n";
			if(header.length() == 0 || footer.length() == 0)
				return;
		}catch (ConcurrentModificationException e) {
			return;
		}

		header = header.substring(0, header.length()-1); // remove the empty line at the end
		footer = footer.substring(0, footer.length()-1); // remove the empty line at the end
		
		// Send tablist
		if(PowerBoard.getBukkitVersion().compareTo(new Version("1.18")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.18"))){
			version_1_17.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.17")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.17"))){
			version_1_17.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.16")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.16"))){
			version_1_16.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.15")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.15"))){
			version_1_15.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.14")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.14"))){
			version_1_14.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.13"))){
			version_1_13.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.12")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.12"))){
			version_1_12.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.11")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.11"))){
		  version_1_11.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.10")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.10"))){
			version_1_10.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.9")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.9"))){
			version_1_08.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.8")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.8"))){
			version_1_08.sendTab(p, header, footer);
		}else
			pl.getLogger().severe("You are using a version that is not supported!");
	}
}
