package me.blahberrys.meteorloot.commands;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.meteor.Meteor;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Spawn {

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandException {
		if (!(sender instanceof Player))
			return true;
		final Player player = (Player) sender;

		if (!(player.hasPermission("meteorloot.spawn"))) {
			MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().noPermMsg);
			return true;
		}

		Location l = player.getLocation().clone();
		final Location toLoc = new Location(player.getWorld(), l.getX(), l.getWorld().getHighestBlockYAt(l), l.getZ());
		final Location fromLoc = new Location(player.getWorld(), l.getX(), Settings.getInstance().spawnHeight, l.getZ());

		Meteor.getInstance().spawned.add(toLoc.clone());
		MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().spawnMeteorMsg);
		
		new BukkitRunnable() {
			public void run() {
				Meteor.getInstance().shootFromTo(fromLoc.clone(), toLoc.clone(), Boolean.TRUE);
			}
		}.runTaskLater(MeteorLoot.getInstance(), 100L);
		return true;
	}

}
