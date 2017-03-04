package me.blahberrys.meteorloot.utils;

import java.util.Random;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class LocationUtil {

	private static boolean safeLocation(Location loc) {
		boolean safe = true;
		if (MeteorLoot.getInstance().townySupport) {
			return !TownyUniverse.getTownBlock(loc).hasTown();
		} else if (MeteorLoot.getInstance().factionsSupport) {
			return MeteorLoot.getInstance().factionsManager.getFactions().isFactionOffline(loc);
		} else if (MeteorLoot.getInstance().worldGuardSupport) {
			for (ProtectedRegion region : WorldGuardPlugin.inst().getRegionManager(loc.getWorld()).getRegions().values())
				return !region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		} else if (MeteorLoot.getInstance().griefPreventionSupport) {
			return GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) == null;
		} else if (loc.getBlock().getBiome() == Biome.OCEAN || loc.getBlock().getBiome() == Biome.DEEP_OCEAN) {
			return false;
		} else {
			for (Location l : Selection.getCube(loc.clone(), 5))
				if (l.getBlock().getType() == Material.WATER || l.getBlock().getType() == Material.STATIONARY_WATER)
					return false;
		}
		return safe;
	}

	public static Location getMeteorCrashLocation(World world) {
		Location l = getRandomLocation(world);
		l.setY(l.getWorld().getHighestBlockYAt(l));
		return l.clone();
	}

	public static Location getRandomLocation(World world) {
		Random r = new Random();
		int radius = r.nextInt(Settings.getInstance().maxBlocksFromSpawn - Settings.getInstance().minBlocksFromSpawn) + Settings.getInstance().minBlocksFromSpawn;
		Location center = world.getSpawnLocation();
		double a = r.nextDouble() * 2 * Math.PI;
		double dist = r.nextDouble() * radius;

		Location loc = center.clone().add(dist * Math.sin(a), 0, dist * Math.cos(a));
		while (!safeLocation(loc)) {
			if (r.nextInt(100) > 50)
				loc = center.clone().add(dist * Math.sin(a), 0, dist * Math.cos(a));
			else
				loc = center.clone().subtract(dist * Math.sin(a), 0, dist * Math.cos(a));
		}
		return loc.clone();
	}

	public static class serialization {
		public static String locToString(Location l) {
			StringBuilder sb = new StringBuilder();
			sb.append(l.getWorld().getName()).append(":");
			sb.append(l.getX()).append(":");
			sb.append(l.getY()).append(":");
			sb.append(l.getZ()).append(":");
			sb.append(l.getYaw()).append(":");
			sb.append(l.getPitch());
			return sb.toString();
		}

		public static String blockLocToString(Location l) {
			StringBuilder sb = new StringBuilder();
			sb.append(l.getWorld().getName()).append(":");
			sb.append(l.getX()).append(":");
			sb.append(l.getY()).append(":");
			sb.append(l.getZ());
			return sb.toString();
		}

		public static Location stringToLoc(String s) {
			try {
				String[] args = s.split(":", 6);
				if (args.length != 6)
					throw new IllegalArgumentException("Invalid location. It did not contain all the parts");

				World w = Bukkit.getServer().getWorld(args[0]);
				double x = Double.parseDouble(args[1]);
				double y = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				float yaw = Float.parseFloat(args[4]);
				float pitch = Float.parseFloat(args[5]);
				if (w == null)
					throw new IllegalStateException("World cannot be null");
				return new Location(w, x, y, z, yaw, pitch);
			} catch (Exception ex) {
				ex.printStackTrace();
				Bukkit.getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "Failed to load location due to error: " + ex.getMessage());
				return null;
			}
		}

		public static Location blockStringToLoc(String s) {
			try {
				String[] args = s.split(":", 4);
				if (args.length != 4)
					throw new IllegalArgumentException("Invalid location. It did not contain all the parts");

				World w = Bukkit.getServer().getWorld(args[0]);
				double x = Double.parseDouble(args[1]);
				double y = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				if (w == null)
					throw new IllegalStateException("World cannot be null");
				return new Location(w, x, y, z);
			} catch (Exception ex) {
				ex.printStackTrace();
				Bukkit.getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "Failed to load location due to error: " + ex.getMessage());
				return null;
			}
		}

	}

}
