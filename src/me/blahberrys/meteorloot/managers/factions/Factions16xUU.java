package me.blahberrys.meteorloot.managers.factions;

import org.bukkit.Location;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

public class Factions16xUU implements FactionsHook {

	@Override
	public boolean isFactionOffline(Location loc) {
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(loc));
		if (!faction.isNormal())
			return false;
		return faction.getOnlinePlayers().size() == 0;
	}

	@Override
	public String getVersion() {
		return "1.6.9-UU";
	}
}
