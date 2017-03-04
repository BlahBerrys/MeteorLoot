package me.blahberrys.meteorloot.commands;

import me.blahberrys.meteorloot.MeteorLoot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("MeteorLoot")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("CreateMeteor"))
					return CreateMeteor.onCommand(sender, cmd, label, args);

				if (args[0].equalsIgnoreCase("Fix"))
					return Fix.onCommand(sender, cmd, label, args);

				if (args[0].equalsIgnoreCase("Reload"))
					return Reload.onCommand(sender, cmd, label, args);

				if (args[0].equalsIgnoreCase("Spawn"))
					return Spawn.onCommand(sender, cmd, label, args);

				if (args[0].equalsIgnoreCase("Start"))
					return Start.onCommand(sender, cmd, label, args);

				if (args[0].equalsIgnoreCase("Time"))
					return Time.onCommand(sender, cmd, label, args);
			} else {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					player.sendMessage(ChatColor.UNDERLINE + "" + ChatColor.DARK_PURPLE + "Meteor" + ChatColor.GOLD + "Loot " + ChatColor.DARK_GRAY + MeteorLoot.getInstance().getDescription().getVersion() + ChatColor.GOLD + "  (" + ChatColor.DARK_PURPLE + "Author: " + ChatColor.GRAY + "BlahBerrys" + ChatColor.GOLD + ")");
					if (player.hasPermission("meteorloot.admin")) {
						player.sendMessage(ChatColor.DARK_GRAY + "__________________________________________");
						player.sendMessage(" ");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "CreateMeteor");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Selector");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Reload");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Start");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Spawn");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Time");
						player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Fix");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_PURPLE + "Meteor" + ChatColor.GOLD + "Loot " + ChatColor.DARK_GRAY + MeteorLoot.getInstance().getDescription().getVersion() + ChatColor.GOLD + "  (" + ChatColor.DARK_PURPLE + "Author: " + ChatColor.GRAY + "BlahBerrys" + ChatColor.GOLD + ")");
					sender.sendMessage(ChatColor.DARK_GRAY + "__________________________________________");
					sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Reload");
					sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Start");
					sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Time");
					sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.GRAY + "Fix");
					sender.sendMessage(ChatColor.DARK_GRAY + "__________________________________________");
				}
				return true;
			}
			return false;
		}
		return false;
	}
}
