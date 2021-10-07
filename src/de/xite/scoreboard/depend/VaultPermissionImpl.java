package de.xite.scoreboard.depend;

import net.milkbowl.vault.permission.Permission;

public class VaultPermissionImpl extends Permission {

	@Override
	public String[] getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "PowerBoard";
	}

	@Override
	public String[] getPlayerGroups(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrimaryGroup(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean groupAdd(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean groupHas(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean groupRemove(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasGroupSupport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSuperPermsCompat() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean playerAdd(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerAddGroup(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerHas(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerInGroup(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerRemove(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerRemoveGroup(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

}
