package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.Bukkit;

import de.xite.scoreboard.main.PowerBoard;

public class Updater {
	private static PowerBoard pl = PowerBoard.pl;
	private static int pluginID = 73854;
	private static String version;
	
	public static String getVersion() {
		if(version == null) {
			try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + pluginID).openStream(); Scanner scanner = new Scanner(inputStream)) {
				if(scanner.hasNext()) {
					String d = scanner.next();
					version = d;
					return d;
				}
			} catch (IOException e) {
				pl.getLogger().info("Updater -> Cannot look for updates: " + e.getMessage());
				return "Could not check for updates! You probably restarted your server to often, so SpigotMC blocked your IP. You probably have to wait a few minutes or hours.";
			}
			
			// Set it to null again after an hour to check again (there might be a new version)
			Bukkit.getScheduler().runTaskLaterAsynchronously(PowerBoard.pl, new Runnable() {
				@Override
				public void run() {
					version = null;
				}
			}, 20*60*60);
		}
		return version;
	}
	
	public static boolean checkVersion() {
		if(getVersion().equals(pl.getDescription().getVersion()))
			return false;
		return true;
	}
	public static boolean downloadFile() {
		try {
			pl.getLogger().info("Updater -> Downloading newest version...");
			File file = new File("plugins/"+PowerBoard.pl.getDescription().getName()+".jar");
			if(!file.exists()) {
				try {
					file.createNewFile();
		        }catch(IOException e) {
		        	e.printStackTrace();
		        	return false;
		        } 
			}
			String url = "https://github.com/Xitee1/PowerBoard/releases/latest/download/"+PowerBoard.pl.getDescription().getName()+".jar";
			HttpURLConnection connection = (HttpURLConnection)(new URL(url)).openConnection();
			connection.connect();
			FileOutputStream outputStream = new FileOutputStream(file);
			InputStream inputStream = connection.getInputStream();
			byte[] buffer = new byte[1024];
			int readBytes = 0;    
			while ((readBytes = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, readBytes);
			}
			outputStream.close();
			inputStream.close();
			connection.disconnect();
			pl.getLogger().info("Updater -> Download finished! To apply the new update, you have to restart your server.");
			return true;
		}catch(Exception e) {
			pl.getLogger().info("Updater -> Download failed! Please try it later again.");
			e.printStackTrace();
			return false;
		}
	}
}
