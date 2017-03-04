package me.blahberrys.meteorloot.managers.factions;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

import me.blahberrys.meteorloot.Settings;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Factions26x implements FactionsHook {

	@Override
	public boolean isFactionOffline(Location loc) {
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(loc));
		if (faction.isNone() || ChatColor.stripColor(faction.getName()).equalsIgnoreCase(Settings.getInstance().warzoneName) || ChatColor.stripColor(faction.getName()).equalsIgnoreCase(Settings.getInstance().safezoneName))
			return false;
		return faction.isFactionConsideredOffline();
	}

	@Override
	public String getVersion() {
		return "2.6.X";
	}

}
