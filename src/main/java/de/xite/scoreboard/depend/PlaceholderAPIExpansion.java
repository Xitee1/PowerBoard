package de.xite.scoreboard.depend;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.TPS;
import de.xite.scoreboard.utils.Teams;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

	private static final List<String> playerPlaceholders = new ArrayList<String>() {
		{
			add("prefix");
			add("suffix");
			add("chat_prefix");
			add("display_name");
		}
	};

	@Override
	public String getAuthor() {
		return String.join(", ", PowerBoard.pl.getDescription().getAuthors());
	}

	@Override
	public String getIdentifier() {
		return PowerBoard.pl.getDescription().getName();
	}

	@Override
	public String getVersion() {
		return PowerBoard.pl.getDescription().getVersion();
	}
	
    @Override
    public String onPlaceholderRequest(Player p, String placeholder) {
	    if(placeholder.equalsIgnoreCase("tps"))
		    return String.valueOf(TPS.getTPS());

	    if(playerPlaceholders.contains(placeholder)) {
		    if(p == null)
			    return "Player not online";

		    // Player specific placeholders below
		    Teams t = Teams.get(p);
		    if(t == null)
			    return "No rank";


		    if(placeholder.equalsIgnoreCase("prefix"))
			    return t.getPrefix();

		    if(placeholder.equalsIgnoreCase("suffix"))
			    return t.getSuffix();

		    if(placeholder.equalsIgnoreCase("chat_prefix"))
			    return t.getChatPrefix();

		    if(placeholder.equalsIgnoreCase("display_name"))
			    return t.getRankDisplayName();
	    }

        return null; // Placeholder is unknown by the Expansion
    }
}
