package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import de.xite.scoreboard.main.PowerBoard;

public class Updater {
	private static final String updaterPrefix = "Updater -> ";
	private static final PowerBoard instance = PowerBoard.getInstance();
	private static final Logger logger = PowerBoard.getInstance().getLogger();
	private static Date lastUpdated;

	private final String repo;
	private final int pluginID;
	private String latestVersion;
	private final String currentVersion;
	private boolean updateCheckEnabled;
	private boolean infoMessageEnabled;
	private boolean updateSuccessful = false;

	public Updater(String repo, int pluginID) {
		this.repo = repo;
		this.pluginID = pluginID;
		latestVersion = null;
		currentVersion = instance.getDescription().getVersion();
		loadConfig();
	}

	private void updateVersion() {
		if(lastUpdated == null)
			lastUpdated = new Date();

		if(!updateCheckEnabled) {
			latestVersion = getCurrentVersion();
		}else if(latestVersion == null || new Date().getTime() - lastUpdated.getTime() > 1000*60*60*12) {
			lastUpdated = new Date();
			try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + pluginID).openStream(); Scanner scanner = new Scanner(inputStream)) {
				if(scanner.hasNext()) {
					latestVersion = scanner.next();
				}
			} catch (IOException e) {
				logger.info("Updater -> Cannot look for updates: " + e.getMessage());
			}
		}
	}

	public String getLatestVersion() {
		updateVersion();
		return latestVersion;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public boolean isUpdateAvailable() {
		Version current = new Version(getCurrentVersion());
		Version newest = new Version(getLatestVersion());
		return current.compareTo(newest) < 0;
	}

	public boolean infoMessageEnabled() {
		return infoMessageEnabled;
	}

	public boolean isUpdateCheckEnabled() {
		return updateCheckEnabled;
	}

	/**
	 * Downloads the latest release from GitHub and replaces the scoreboard jar inside the "plugins" folder.
	 *
	 * @param forceUpdate
	 * @return
	 */
	public boolean downloadFile(boolean forceUpdate) {
		if(this.updateSuccessful) {
			logger.warning("Updater -> Ignoring update request. The plugin has already been updated. Please restart your server for the update to take affect.");
			return false;
		}

		String pluginName = PowerBoard.pl.getDescription().getName();

		if(!new File("plugins/" + pluginName + ".jar").exists()) {
			logger.severe("Updater -> Built in plugin updater only works if the jar file is named 'PowerBoard.jar'");
			return false;
		}

		try {
			// Download new PowerBoard.jar to plugins/PowerBoard.update.jar
			logger.info("Updater -> Downloading newest version...");
			File file = new File("plugins/" + pluginName + ".jar.update");
			if(forceUpdate)
				file = new File("plugins/" + pluginName + ".jar");

			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

			String url = "https://github.com/"+repo+"/releases/latest/download/" + pluginName + ".jar";
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
			logger.info(updaterPrefix+"Download failed! Please try it later again.");
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
				logger.severe(updaterPrefix+"Could not delete 'PowerBoard.jar.old'!");
				return false;
			}
		}

		// PowerBoard.jar -> PowerBoard.old.jar
		try {
			FileUtils.moveFile(currentFile, oldFile);
		} catch (IOException e) {
			logger.severe(updaterPrefix+"Could not rename current PowerBoard.jar file.");
			e.printStackTrace();
			return false;
		}

		// PowerBoard.update.jar -> PowerBoard.jar
		if(!currentFile.exists()) {
			try {
				FileUtils.moveFile(newFile, currentFile);
			} catch (IOException e) {
				logger.severe(updaterPrefix+"Could not rename new PowerBoard.jar file.");
				e.printStackTrace();
				return false;
			}
		}else {
			logger.severe(updaterPrefix+"Old file still exists. Could not update PowerBoard!");
		}

		updateSuccessful = true;
		logger.info(updaterPrefix+"Update finished! To apply the new update, you have to restart your server.");
		return true;
	}

	public void loadConfig() {
		updateCheckEnabled = instance.getConfig().getBoolean("update.checkForUpdates");
		infoMessageEnabled = instance.getConfig().getBoolean("update.notification");
	}
}
