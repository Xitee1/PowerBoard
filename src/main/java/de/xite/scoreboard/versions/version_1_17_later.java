package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

public class version_1_17_later extends VersionSpecific {

    @Override
    public int getPing(Player p) {
        return p.getPing();
    }

    @Override
    public void sendTab(Player p, String header, String footer) {
        p.setPlayerListHeaderFooter(header, footer);
    }
}