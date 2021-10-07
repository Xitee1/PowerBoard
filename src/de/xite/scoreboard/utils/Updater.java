package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.Bukkit;

import de.xite.scoreboard.main.Main;

public class Updater {
	private static Main pl = Main.pl;
	private static int pluginID = 73854;
	public static String version;
    public static String getVersion() {
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + pluginID).openStream(); Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
            	String d = scanner.next();
            	version = d;
                return d;
            }
        } catch (IOException e) {
            pl.getLogger().info("Updater -> Cannot look for updates: " + e.getMessage());
        }
        return "Could not check for updates! You probably restarted your server to often, so SpigotMC blocked your IP. You probably have to wait a few minutes/hours.";
    }
    
    public static boolean checkVersion() {
    	if(version == null) {
    		version = getVersion();
    		// Set it to null again after an hour to check again
    		Bukkit.getScheduler().runTaskLater(Main.pl, new Runnable() {
				@Override
				public void run() {
					version = null;
				}
			}, 20*60*60);
    	}
    	
    	if(version.equals(pl.getDescription().getVersion()))
    		return false;
    	return true;
    }
	public static boolean downloadFile() {
	    try {
	    	pl.getLogger().info("Updater -> Downloading newest version...");
			File file = new File("plugins/"+Main.pl.getDescription().getName()+".jar");
			if(!file.exists()) {
				try {
					file.createNewFile();
		        }catch(IOException e) {
		        	e.printStackTrace();
		        	return false;
		        } 
			}
	    	String url = "https://xitecraft.de/downloads/"+Main.pl.getDescription().getName()+".jar";
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
