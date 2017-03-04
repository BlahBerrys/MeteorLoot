package me.blahberrys.meteorloot.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Selection {

	public static List<Location> getCircle(Location loc, int radius, boolean hollow, boolean sphere) {
		List<Location> circleblocks = new ArrayList<Location>();
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();

		for (int x = cx - radius; x <= cx + radius; x++) {
			for (int z = cz - radius; z <= cz + radius; z++) {
				for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + radius); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
						Location l = new Location(loc.getWorld(), x, y + radius, z);
						circleblocks.add(l);
					}
				}
			}
		}

		return circleblocks;
	}

	public static List<Location> getCube(Location loc, int radius) {
		List<Location> cubeBlocks = new ArrayList<Location>();

		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++)
					cubeBlocks.add(loc.clone().add(0, radius, 0).getBlock().getRelative(x, y, z).getLocation());

		return cubeBlocks;
	}

}
