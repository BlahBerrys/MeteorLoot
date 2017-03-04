package me.blahberrys.meteorloot.meteor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.handlers.BlockHandler;
import me.blahberrys.meteorloot.handlers.ChestHandler;
import me.blahberrys.meteorloot.handlers.TimeHandler;
import me.blahberrys.meteorloot.utils.LocationUtil;
import me.blahberrys.meteorloot.utils.SchematicUtils;
import me.blahberrys.meteorloot.utils.Selection;
import net.minecraft.server.v1_10_R1.EntityTypes;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Meteor {

	public static Meteor instance = new Meteor();

	public static Meteor getInstance() {
		return instance;
	}

	public HashMap<World, Boolean> inProgress = new HashMap<World, Boolean>();
	public HashMap<World, Boolean> announced = new HashMap<World, Boolean>();

	public HashMap<World, Location> crashLocs = new HashMap<World, Location>();

	public HashMap<World, ArrayList<Location>> meteorBlocks = new HashMap<World, ArrayList<Location>>();
	public HashMap<World, HashMap<Location, Material>> blocksToRepair = new HashMap<World, HashMap<Location, Material>>();
	public ArrayList<Location> spawned = new ArrayList<Location>();

	public void patchMeteor() {
		try {
			Method a = EntityTypes.class.getDeclaredMethod("a", new Class[] { Class.class, String.class, Integer.TYPE });
			a.setAccessible(true);
			a.invoke(a, new Object[] { EntityMeteor.class, "Meteor", 1337 });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startMeteorCrash(World world) {
		inProgress.put(world, true);
		announced.put(world, false);

		Location crashLoc = LocationUtil.getMeteorCrashLocation(world);
		final Location toLoc = crashLoc.clone();
		crashLoc.setY(Settings.getInstance().spawnHeight);
		final Location fromLoc = crashLoc.clone();
		crashLocs.put(world, toLoc.clone());

		toLoc.getChunk().load();
		BlockHandler.addBlocksToRepair(toLoc.getWorld(), BlockHandler.getBlocksInRadius(toLoc.clone(), Settings.getInstance().explosionRadius * 2), false);
		new BukkitRunnable() {
			public void run() {
				shootFromTo(fromLoc, toLoc, Boolean.FALSE);
			}
		}.runTaskLater(MeteorLoot.getInstance(), 100L);
	}

	public void shootFromTo(Location fromLoc, Location toLoc, Boolean forceSpawned) {
		CraftWorld handle = (CraftWorld) fromLoc.getWorld();
		EntityMeteor mm = new EntityMeteor(handle.getHandle(), forceSpawned, new MeteorShape());

		Fireball meteor = (Fireball) mm.getBukkitEntity();
		meteor.setIsIncendiary((Settings.getInstance().fireChance > 0 ? true : false));
		meteor.teleport(fromLoc);
		meteor.setBounce(false);
		mm.spawn(fromLoc);

		mm.setPosition(fromLoc.getX(), fromLoc.getY(), fromLoc.getZ());
		meteor.setDirection(toLoc.toVector().subtract(fromLoc.toVector()));
		meteor.setVelocity(meteor.getVelocity().multiply(2));

		// mm.spawn(fromLoc);
		// mm.setPosition(fromLoc.getX(), fromLoc.getY(), fromLoc.getZ());
	}

	public void createAftermath(Location loc, boolean forceSpawned, MeteorShape shape) {
		loc.setY(loc.getWorld().getHighestBlockYAt(loc));

		for (String s : Settings.getInstance().soundEffects)
			loc.getWorld().playSound(loc, Sound.valueOf(s), Math.round(Settings.getInstance().meteorRadius), Math.round(Settings.getInstance().meteorRadius));
		for (String s : Settings.getInstance().effects)
			loc.getWorld().playEffect(loc, Effect.valueOf(s), Math.round(Settings.getInstance().explosionRadius));

		createMeteor(shape, loc.clone());
		if (shape.getShape() != MeteorShape.Shape.SCHEMATIC)
			ChestHandler.dropLoot(loc.clone().add(0, (Settings.getInstance().meteorRadius), 0));

		if (!forceSpawned) {
			String s = "X: " + loc.getBlockX() + ", Y: " + loc.getWorld().getHighestBlockYAt(loc) + ", Z: " + loc.getBlockZ();
			MeteorLoot.getInstance().broadcastMessage(loc.getWorld(), Settings.getInstance().landingMsg.replaceAll("@LOCATION", s));
			inProgress.put(loc.getWorld(), false);
			TimeHandler.setNextDropTime(loc.getWorld());
			TimeHandler.waitTimes.put(loc.getWorld(), Settings.getInstance().timeTilRepair);
		}
	}

	@SuppressWarnings("deprecation")
	private void createMeteor(MeteorShape shape, Location loc) {
		Random r = new Random();
		int radius = Settings.getInstance().meteorRadius;
		BlockHandler.addBlocksToRepair(loc.getWorld(), BlockHandler.getBlocksInRadius(loc.clone(), radius), false);
		Iterator<Location> iterator = null;

		// Determine Meteor Shape
		if (shape.getShape() == MeteorShape.Shape.SPHERE) {
			iterator = Selection.getCircle(loc.clone(), radius, Settings.getInstance().hollow, true).iterator();
		} else if (shape.getShape() == MeteorShape.Shape.CUBE) {
			iterator = Selection.getCube(loc.clone(), radius).iterator();
		} else if (shape.getShape() == MeteorShape.Shape.SCHEMATIC) {
			SchematicUtils.paste(shape, loc.clone());
			return;
		}

		// Create Meteor Shape
		if (!meteorBlocks.containsKey(loc.getWorld()))
			meteorBlocks.put(loc.getWorld(), new ArrayList<Location>());
		for (Iterator<Location> locIterator = iterator; locIterator.hasNext();) {
			Location l = locIterator.next();
			meteorBlocks.get(loc.getWorld()).add(l.clone());
			ItemStack randomBlock = Settings.getInstance().blocks.get(r.nextInt(Settings.getInstance().blocks.size()));

			l.getBlock().setType(randomBlock.getType());
			l.getBlock().setData((byte) randomBlock.getData().getData());
		}

		// Create Chest Barrier
		if (Settings.getInstance().barrierBlocks.isEmpty())
			return;
		for (Iterator<Location> nearBlocks = Selection.getCube(loc.clone().add(0, radius - 1, 0), 1).iterator(); nearBlocks.hasNext();) {
			Location l = nearBlocks.next();
			meteorBlocks.get(loc.getWorld()).add(l.clone());

			ItemStack randomBlock = Settings.getInstance().barrier.get(r.nextInt(Settings.getInstance().barrier.size()));
			l.getBlock().setType(randomBlock.getType());
			l.getBlock().setData((byte) randomBlock.getData().getData());
		}
	}
}
