package me.blahberrys.meteorloot.commands;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.utils.SchematicUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateMeteor {

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandException {
		if (!(sender instanceof Player))
			return true;
		final Player player = (Player) sender;

		if (!(player.hasPermission("meteorloot.create"))) {
			MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().noPermMsg);
			return true;
		}
		
		if (args.length != 2) {
			MeteorLoot.getInstance().sendMessage(player, ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.DARK_GRAY + "CreateMeteor " + ChatColor.GRAY + "<Name>");
			return true;
		}
		
		SchematicUtils.save(player, args[1].toUpperCase());
		return false;
	}

}
