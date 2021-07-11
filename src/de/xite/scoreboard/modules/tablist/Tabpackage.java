package de.xite.scoreboard.modules.tablist;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Main;
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
	static Main pl = Main.pl;

	public static void send(Player p) {
		String header = "", footer = "";
		
		if(TabConfig.headers.isEmpty() || TabConfig.footers.isEmpty()) {
			pl.getLogger().severe("The tablist config file is empty or the header/footer is not configurated!");
			return;
		}
		
		for(Entry<Integer, String> e : TabConfig.currentHeader.get(p).entrySet())
			header += e.getValue()+"\n";
		for(Entry<Integer, String> e : TabConfig.currentFooter.get(p).entrySet())
			footer += e.getValue()+"\n";
		header = header.substring(0, header.length()-1); //remove the empty line at the end
		footer = footer.substring(0, footer.length()-1); //remove the empty line at the end
		
		//Tablist senden
		if(Main.getBukkitVersion().equals(new Version("1.17"))){
			version_1_17.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.16"))){
			version_1_16.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.15"))){
			version_1_15.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.14"))){
			version_1_14.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.13"))){
			version_1_13.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.12"))){
			version_1_12.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.11"))){
		  version_1_11.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.10"))){
			version_1_10.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.9"))){
			version_1_08.sendTab(p, header, footer);
		}else if(Main.getBukkitVersion().equals(new Version("1.8"))){
			version_1_08.sendTab(p, header, footer);
		}else
			pl.getLogger().severe("You are using a version that is not supported! If you think this version should work, please report it to our discord server!");
	}
}
