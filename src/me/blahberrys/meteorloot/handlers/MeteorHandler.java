package me.blahberrys.meteorloot.handlers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.meteor.Meteor;
import me.blahberrys.meteorloot.utils.FacePaste;
import me.blahberrys.meteorloot.utils.ImageChar;
import me.blahberrys.meteorloot.utils.ImageMessage;
import me.blahberrys.meteorloot.utils.Selection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MeteorHandler implements Listener {

	public static ArrayList<UUID> meteors = new ArrayList<UUID>();

	@EventHandler
	public void onDestroyObsidian(EntityExplodeEvent event) {
		if (event.getEntity() == null || event.getEntity().getType() != EntityType.PRIMED_TNT)
			return;
		Location tntLoc = event.getLocation();
		if (!Settings.getInstance().worlds.contains(tntLoc.getWorld()) || !Meteor.getInstance().meteorBlocks.containsKey(tntLoc.getWorld()))
			return;

		for (Iterator<Location> nearBlocks = Selection.getCube(tntLoc.clone().subtract(0, 1, 0), 3).iterator(); nearBlocks.hasNext();) {
			Block b = nearBlocks.next().getBlock();

			if (b == null || b.getType() != Material.OBSIDIAN)
				continue;
			if (Meteor.getInstance().meteorBlocks.get(tntLoc.getWorld()).contains(b.getLocation()))
				b.breakNaturally();
		}
		for (Iterator<Block> blocks = event.blockList().iterator(); blocks.hasNext();) {
			Block b = blocks.next();
			if (b.getState() instanceof Chest && Meteor.getInstance().meteorBlocks.get(b.getWorld()).contains(b.getLocation()))
				blocks.remove();
		}
	}

	@EventHandler
	public void onOpenMeteorChest(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST)
			return;

		Location loc = event.getClickedBlock().getLocation().clone();

		if (!Settings.getInstance().worlds.contains(loc.getWorld()))
			return;
		if (!Meteor.getInstance().announced.containsKey(loc.getWorld()) || Meteor.getInstance().announced.get(loc.getWorld()))
			return;

		boolean forced = false;
		for (Iterator<Location> forcedSpawned = Meteor.getInstance().spawned.iterator(); forcedSpawned.hasNext();) {
			Location l = forcedSpawned.next();
			if (l.distance(loc) <= Settings.getInstance().explosionRadius) {
				forced = true;
				break;
			}
		}
		if (!forced) {
			if (Meteor.getInstance().meteorBlocks.get(loc.getWorld()).contains(loc)) {
				if (Settings.getInstance().firstLootMsg.contains("@FACE")) {
					BufferedImage face = FacePaste.getFaceImage(event.getPlayer().getName());
					new ImageMessage(face, 8, ImageChar.MEDIUM_SHADE.getChar()).sendToPlayers(event.getPlayer().getWorld().getPlayers());
				}
				MeteorLoot.getInstance().broadcastMessage(loc.getWorld(), Settings.getInstance().firstLootMsg.replace("@FACE", "").replace("@PLAYER", event.getPlayer().getName()));
				Meteor.getInstance().announced.put(loc.getWorld(), true);
			}
		}
	}

	@EventHandler
	public void onChestDestory(BlockBreakEvent event) {
		Block b = event.getBlock();

		if (!Settings.getInstance().worlds.contains(b.getWorld()) || !Meteor.getInstance().meteorBlocks.containsKey(b.getWorld()))
			return;
		if (Meteor.getInstance().meteorBlocks.get(b.getWorld()).contains(b.getLocation()) && event.getBlock().getType() == Material.CHEST)
			event.setCancelled(true);
	}

	@EventHandler
	public void onObsidianDestroy(BlockBreakEvent event) {
		Block b = event.getBlock();

		if (!Settings.getInstance().worlds.contains(b.getWorld()) || !Meteor.getInstance().meteorBlocks.containsKey(b.getWorld()))
			return;
		if (!Settings.getInstance().canMine)
			if (Meteor.getInstance().meteorBlocks.get(b.getWorld()).contains(b.getLocation()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onMeteorCrash(EntityExplodeEvent event) {
		if (event.getEntity() == null)
			return;
		if (meteors.contains(event.getEntity().getUniqueId())) {
			if (event.getEntity().isDead())
				meteors.remove(event.getEntity().getUniqueId());

			event.blockList().clear();
		}
	}

}
