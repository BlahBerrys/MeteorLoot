package me.blahberrys.meteorloot.commands;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Selector {

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandException {
		Player player = (Player) sender;

		if (args.length == 1) {
			if (!(player.hasPermission("meteorloot.admin"))) {
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().noPermMsg);
				return true;
			}
			
			player.getInventory().addItem(Settings.getInstance().selectorWand.clone());
			player.updateInventory();
		}
		return false;
	}

}
