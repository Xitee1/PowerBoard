package de.xite.scoreboard.depend;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class VaultChatImpl extends Chat{

	public VaultChatImpl(Permission perms) {
		super(perms);
		
	}

	@Override
	public boolean getGroupInfoBoolean(String arg0, String arg1, String arg2, boolean arg3) {
		
		return false;
	}

	@Override
	public double getGroupInfoDouble(String arg0, String arg1, String arg2, double arg3) {
		
		return 0;
	}

	@Override
	public int getGroupInfoInteger(String arg0, String arg1, String arg2, int arg3) {
		
		return 0;
	}

	@Override
	public String getGroupInfoString(String arg0, String arg1, String arg2, String arg3) {
		
		return null;
	}

	@Override
	public String getGroupPrefix(String arg0, String arg1) {
		return null;
	}

	@Override
	public String getGroupSuffix(String arg0, String arg1) {
		return null;
	}

	@Override
	public String getName() {
		return "PowerBoard";
	}

	@Override
	public boolean getPlayerInfoBoolean(String arg0, String arg1, String arg2, boolean arg3) {
		
		return false;
	}

	@Override
	public double getPlayerInfoDouble(String arg0, String arg1, String arg2, double arg3) {
		
		return 0;
	}

	@Override
	public int getPlayerInfoInteger(String arg0, String arg1, String arg2, int arg3) {
		
		return 0;
	}

	@Override
	public String getPlayerInfoString(String arg0, String arg1, String arg2, String arg3) {
		
		return null;
	}

	@Override
	public String getPlayerPrefix(String arg0, String arg1) {
		return null;
	}

	@Override
	public String getPlayerSuffix(String arg0, String arg1) {
		return null;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

	@Override
	public void setGroupInfoBoolean(String arg0, String arg1, String arg2, boolean arg3) {
		
		
	}

	@Override
	public void setGroupInfoDouble(String arg0, String arg1, String arg2, double arg3) {
		
		
	}

	@Override
	public void setGroupInfoInteger(String arg0, String arg1, String arg2, int arg3) {
		
		
	}

	@Override
	public void setGroupInfoString(String arg0, String arg1, String arg2, String arg3) {
		
		
	}

	@Override
	public void setGroupPrefix(String arg0, String arg1, String arg2) {
		
		
	}

	@Override
	public void setGroupSuffix(String arg0, String arg1, String arg2) {
		
		
	}

	@Override
	public void setPlayerInfoBoolean(String arg0, String arg1, String arg2, boolean arg3) {
		
		
	}

	@Override
	public void setPlayerInfoDouble(String arg0, String arg1, String arg2, double arg3) {
		
		
	}

	@Override
	public void setPlayerInfoInteger(String arg0, String arg1, String arg2, int arg3) {
		
		
	}

	@Override
	public void setPlayerInfoString(String arg0, String arg1, String arg2, String arg3) {
		
		
	}

	@Override
	public void setPlayerPrefix(String arg0, String arg1, String arg2) {
		
		
	}

	@Override
	public void setPlayerSuffix(String arg0, String arg1, String arg2) {
		
		
	}

}
