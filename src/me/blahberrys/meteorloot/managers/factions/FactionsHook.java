package me.blahberrys.meteorloot.managers.factions;

import org.bukkit.Location;

public interface FactionsHook {

	public String getVersion();

	public boolean isFactionOffline(Location loc);

}
