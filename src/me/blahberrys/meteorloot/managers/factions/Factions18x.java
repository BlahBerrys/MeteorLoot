package me.blahberrys.meteorloot.managers.factions;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import me.blahberrys.meteorloot.Settings;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Factions18x implements FactionsHook {

	@Override
	public boolean isFactionOffline(Location loc) {
		Faction faction = Board.getInstance().getFactionAt(new FLocation(loc));
		if (faction.isNone() || ChatColor.stripColor(faction.getTag()).equalsIgnoreCase(Settings.getInstance().warzoneName) || ChatColor.stripColor(faction.getTag()).equalsIgnoreCase(Settings.getInstance().safezoneName))
			return false;
		return true;
	}

	@Override
	public String getVersion() {
		return "1.8.X";
	}
}
