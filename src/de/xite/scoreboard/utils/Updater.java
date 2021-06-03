package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
        } catch (IOException exception) {
            pl.getLogger().info("Updater -> Cannot look for updates: " + exception.getMessage());
        }
        return null;
    }
    public static boolean checkVersion() {
    	if(getVersion().equals(pl.getDescription().getVersion())) {
    		return false;
    	}
    	pl.getLogger().info("Updater -> A new version is available!");
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
