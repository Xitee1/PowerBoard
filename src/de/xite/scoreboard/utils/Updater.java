package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import de.xite.scoreboard.main.PowerBoard;

public class Updater {
	static String updaterPrefix = "Updater -> ";
	static boolean updateSuccessful = false;

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
				pl.getLogger().info(updaterPrefix + "Cannot look for updates: " + e.getMessage());
				return "Could not check for updates! You probably restarted your server to often, so SpigotMC blocked your IP. You probably have to wait a few minutes or hours.";
			}
			
			// Set it to null again after 24h to check again (there might be a new version)
			Bukkit.getScheduler().runTaskLaterAsynchronously(PowerBoard.pl, () -> version = null, 20*60*60*24);
		}
		return version;
	}
	
	public static boolean checkVersion() {
		Version current = new Version(pl.getDescription().getVersion());
		Version newest = new Version(getVersion());

		return current.compareTo(newest) < 0;
	}
	public static boolean downloadFile(boolean forceUpdate) {
		if(updateSuccessful) {
			pl.getLogger().info("Ignoring update request. Plugin has already been updated.");
			return false;
		}

		String pluginName = PowerBoard.pl.getDescription().getName();

		try {
			// Download new PowerBoard.jar to plugins/PowerBoard.update.jar
			pl.getLogger().info("Updater -> Downloading newest version...");
			File file = new File("plugins/" + pluginName + ".jar.update");
			if(forceUpdate)
				file = new File("plugins/" + pluginName + ".jar");

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			String url = "https://github.com/Xitee1/PowerBoard/releases/latest/download/" + pluginName + ".jar";
			HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
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
		} catch (Exception e) {
			pl.getLogger().info(updaterPrefix+"Download failed! Please try it later again.");
			e.printStackTrace();
			return false;
		}

		if(forceUpdate)
			return true;

		// Move new file in place

		File newFile = new File("plugins/" + pluginName + ".jar.update");
		File oldFile = new File("plugins/" + pluginName + ".jar.old");
		File currentFile = new File("plugins/" + pluginName + ".jar");

		// Delete PowerBoard.old.jar if exists
		if (oldFile.exists()) {
			if (!oldFile.delete()) {
				pl.getLogger().severe(updaterPrefix+"Could not delete PowerBoard.old.jar even tough it exists!");
				return false;
			}
		}

		// PowerBoard.jar -> PowerBoard.old.jar
		try {
			FileUtils.moveFile(currentFile, oldFile);
		} catch (IOException e) {
			pl.getLogger().severe(updaterPrefix+"Could not rename current PowerBoard.jar file.");
			e.printStackTrace();
			return false;
		}

		// PowerBoard.update.jar -> PowerBoard.jar
		if(!currentFile.exists()) {
			try {
				FileUtils.moveFile(newFile, currentFile);
			} catch (IOException e) {
				pl.getLogger().severe(updaterPrefix+"Could not rename new PowerBoard.jar file.");
				e.printStackTrace();
				return false;
			}
		}else {
			pl.getLogger().severe(updaterPrefix+"Old file still exists. Could not update PowerBoard!");
		}


		// Clear files
		if(!newFile.delete()) {
			pl.getLogger().warning(updaterPrefix+"Could not delete update-file. Please manually delete plugins/"+pluginName+".update.jar");
		}

		updateSuccessful = true;
		pl.getLogger().info(updaterPrefix+"Update finished! To apply the new update, you have to restart your server.");
		return true;
	}
}
