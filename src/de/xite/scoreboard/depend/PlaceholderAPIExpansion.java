package de.xite.scoreboard.depend;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.TPS;
import de.xite.scoreboard.utils.Teams;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "Xitee";
	}
	@Override
	public String getIdentifier() {
		return "PowerBoard";
	}
	@Override
	public String getVersion() {
		return PowerBoard.pl.getDescription().getVersion();
	}
	
    @Override
    public String onPlaceholderRequest(Player p, String placeholder) {
        if(placeholder.equalsIgnoreCase("tps"))
            return TPS.getTPS()+"";
    	
    	if(p == null)
    		return "Player not online";
    	Teams t = Teams.get(p);
    	if(t == null)
    		return "No team";
    	
        if(placeholder.equalsIgnoreCase("prefix"))
            return t.getPrefix();
        
        if(placeholder.equalsIgnoreCase("suffix"))
            return t.getSuffix();
        
        if(placeholder.equalsIgnoreCase("chat_prefix"))
            return t.getChatPrefix();
        
        if(placeholder.equalsIgnoreCase("display_name"))
            return t.getRankDisplayName();
        
        return null; // Placeholder is unknown by the Expansion
    }
}
