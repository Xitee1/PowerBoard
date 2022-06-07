package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.xite.scoreboard.main.PowerBoard;

public class Logger {
	
	
	public static void writeToFile() {
		try {
			// create / reset logfile
			File logfile = new File(PowerBoard.pluginfolder+"debug-info.txt");
			if(logfile.exists())
				logfile.delete();
			logfile.createNewFile();
			
			// write data
			FileWriter writer = new FileWriter(logfile);
			writer.write("Minecraft-Version: "+Bukkit.getServer().getVersion());
			writer.write("Plugin-Version: "+PowerBoard.pl.getDescription().getVersion());
			writer.write("Installed-Plugins: "+Bukkit.getServer().getPluginManager().getPlugins());
			
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
