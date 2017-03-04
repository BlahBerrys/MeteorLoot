package me.blahberrys.meteorloot.handlers;

import java.util.HashMap;
import java.util.Iterator;

import me.blahberrys.meteorloot.Data;
import me.blahberrys.meteorloot.meteor.Meteor;
import me.blahberrys.meteorloot.utils.Selection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;

public class BlockHandler {

	public static HashMap<Location, Material> getBlocksInRadius(Location loc, int radius) {
		HashMap<Location, Material> data = new HashMap<Location, Material>();
		for (Iterator<Location> iterator = Selection.getCube(loc, radius).iterator(); iterator.hasNext();) {
			Location l = iterator.next();
			data.put(l.clone(), l.getBlock().getType());
		}
		return data;
	}

	public static void addBlocksToRepair(World world, HashMap<Location, Material> data, boolean save) {
		if (!Meteor.getInstance().blocksToRepair.containsKey(world))
			Meteor.getInstance().blocksToRepair.put(world, new HashMap<Location, Material>());

		for (Iterator<World> worldIterator = Meteor.getInstance().blocksToRepair.keySet().iterator(); worldIterator.hasNext();) {
			World w = worldIterator.next();

			if (w == world)
				for (Iterator<Location> locIterator = data.keySet().iterator(); locIterator.hasNext();) {
					Location l = locIterator.next();
					if (!Meteor.getInstance().blocksToRepair.get(w).containsKey(l))
						Meteor.getInstance().blocksToRepair.get(w).put(l.clone(), data.get(l));
				}
		}
		if (save)
			Data.save();
	}

	public static int repairWorldBlocks(World world) {
		if (!Meteor.getInstance().blocksToRepair.containsKey(world) || Meteor.getInstance().blocksToRepair.get(world).isEmpty())
			return 0;
		int fixed = 0;
		for (Iterator<World> locs = Meteor.getInstance().blocksToRepair.keySet().iterator(); locs.hasNext();) {
			World w = locs.next();

			if (world == null || w == world)
				for (Iterator<Location> locIterator = Meteor.getInstance().blocksToRepair.get(w).keySet().iterator(); locIterator.hasNext();) {
					Location l = locIterator.next();

					if (l.getBlock().getState() instanceof Chest)
						((Chest) l.getBlock().getState()).getInventory().clear();

					l.getBlock().setType(Meteor.getInstance().blocksToRepair.get(w).get(l));
					locIterator.remove();
					fixed++;

					if (!locIterator.hasNext())
						for (Entity e : l.getChunk().getEntities())
							e.remove();
				}
			if (Meteor.getInstance().meteorBlocks.containsKey(world))
				Meteor.getInstance().meteorBlocks.get(world).clear();
		}
		Data.save();
		return fixed;
	}
}
