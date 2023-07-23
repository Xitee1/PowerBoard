package de.xite.scoreboard.versions;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Logger;

public abstract class VersionSpecific {
	public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
	protected static final Logger logger = PowerBoard.pl.getLogger();

	public static VersionSpecific current;

    public int getPing(Player p) {
        try {
            Object nmsPlayer = nmsPlayer(p);
	        return nmsPlayer.getClass().getField("ping").getInt(nmsPlayer);
        } catch (ReflectiveOperationException ex) {
            logger.severe("Field 'ping' not found in Player. Unsupported server version/brand?");
            return 0;
        }
    }

    public abstract void sendTab(Player p, String header, String footer);


    public static void init() {
        if (Version.CURRENT.isAtLeast(Version.v1_17)) {
            current = new version_1_17_later();
        } else if (Version.CURRENT.isAtLeast(Version.v1_16)) {
            current = new version_1_16();
        } else if (Version.CURRENT.isAtLeast(Version.v1_15)) {
            current = new version_1_15();
        } else if (Version.CURRENT.isAtLeast(Version.v1_14)) {
            current = new version_1_14();
        } else if (Version.CURRENT.isAtLeast(Version.v1_13)) {
            current = new version_1_13();
        } else if (Version.CURRENT.isAtLeast(Version.v1_12)) {
            current = new version_1_12();
        } else if (Version.CURRENT.isAtLeast(Version.v1_11)) {
            current = new version_1_11();
        } else if (Version.CURRENT.isAtLeast(Version.v1_10)) {
            current = new version_1_10();
        } else if (Version.CURRENT.isAtLeast(Version.v1_9)) {
            current = new version_1_09();
        } else if (Version.CURRENT.isAtLeast(Version.v1_8)) {
            current = new version_1_08();
        } else {
            current = new VersionSpecific() {
                public void sendTab(Player p, String header, String footer) {
                    logger.warning("You are using a Minecraft version that does not support tablist header/footer!");
                }
            };
            logger.warning("You are using a unsupported Minecraft version!");
        }
		if(PowerBoard.debug)
            logger.info("Detected Server Version (NMS): " + NMS_VERSION);
    }


    // ------------------------------ Reflection / NMS stuff ------------------------------

    protected final Class<?> craftPlayer;

    {
        try {
            craftPlayer = Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".entity.CraftPlayer");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected final Object nmsPlayer(Player p) {
        try {
            return craftPlayer.getMethod("getHandle").invoke(p);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected final void sendPacket(Player p, Object packet) {
        try {
            Object nmsPlayer = nmsPlayer(p);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            connection.getClass().getMethod("sendPacket", nmsClass("Packet")).invoke(connection, packet);
        } catch (ReflectiveOperationException ex) {
            logger.severe("An error occurred while trying to send packet: " + ex.getMessage());
        }
    }

    private static final HashMap<String, Class<?>> cache = new HashMap<>();

    protected static Class<?> lookupClass(String name) throws ClassNotFoundException {
        Class<?> clazz = cache.get(name);
        if (clazz == null) {
            clazz = Class.forName(name);
            cache.put(name, clazz);
        }
        return clazz;
    }

    protected static Class<?> nmsClass(String name) throws ClassNotFoundException {
        return lookupClass("net.minecraft.server." + NMS_VERSION + "." + name);
    }
}