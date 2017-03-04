package me.blahberrys.meteorloot.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SelectionHandler implements Listener {

	public static HashMap<UUID, Location> pointA = new HashMap<UUID, Location>();
	public static HashMap<UUID, Location> pointB = new HashMap<UUID, Location>();

	public static boolean hasSelection(Player player) {
		if (pointA.containsKey(player.getUniqueId()) && pointB.containsKey(player.getUniqueId())) {
			Location pA = pointA.get(player.getUniqueId());
			Location pB = pointB.get(player.getUniqueId());

			if (pA == pB) {
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().samePointsMsg);
				return false;
			} else if (!(pA.getWorld() == pB.getWorld())) {
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().diffWorldsMsg);
				return false;
			}
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerSelectPoint(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null)
			return;

		Player player = event.getPlayer();
		Location l = event.getClickedBlock().getLocation();

		if (player.getItemInHand().getType() == Settings.getInstance().selectorWand.getType()) {
			if (!player.hasPermission("meteorloot.selector"))
				return;

			String loc = "X: " + l.getBlockX() + ", Y: " + l.getBlockY() + ", Z: " + l.getBlockZ();
			event.setCancelled(true);

			if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				pointA.put(player.getUniqueId(), l);
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().pointAMsg.replaceAll("@loc", loc));
			} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				pointB.put(player.getUniqueId(), l);
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().pointBMsg.replaceAll("@loc", loc));
			}
		}
	}
}
