package de.xite.scoreboard.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CustomPlaceholders {
	@NotNull
	String replace(Player p, String s);
}