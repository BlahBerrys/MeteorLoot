package me.blahberrys.meteorloot.managers.factions;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;

public class Factions16x implements FactionsHook {

	@Override
	public boolean isFactionOffline(Location loc) {
		Faction faction = Board.getFactionAt(new FLocation(loc));
		if (!faction.isNormal())
			return false;
		return faction.getOnlinePlayers().size() == 0;
	}

	@Override
	public String getVersion() {
		return "1.6.9.X";
	}
}
