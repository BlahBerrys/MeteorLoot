package me.blahberrys.meteorloot.commands;

import java.util.Random;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.handlers.TimeHandler;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Time {

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandException {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;

			if (!(player.hasPermission("meteorloot.time"))) {
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().noPermMsg);
				return true;
			}
		}

		if (args.length > 1) {
			World world = args.length > 2 ? MeteorLoot.getInstance().getServer().getWorld(args[2]) : null;
			world = (world != null ? world : player != null ? player.getWorld() : null);

			if (world == null || !Settings.getInstance().worlds.contains(world)) {
				if (player != null)
					MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().notEnabledMsg);
				else
					sender.sendMessage(Settings.getInstance().notEnabledMsg);
				return true;
			}

			if (args[1].equalsIgnoreCase("Check")) {
				if (args.length < 2 || args.length > 3) {
					if (player != null)
						MeteorLoot.getInstance().sendMessage(player, ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.DARK_GRAY + "Time " + ChatColor.GRAY + " Check <World>");
					else
						sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.DARK_GRAY + "Time " + ChatColor.GRAY + " Check <World>");
					return true;
				}

				if (!TimeHandler.dropTimes.containsKey(world))
					TimeHandler.dropTimes.put(world, getRandomTime());
				int time = TimeHandler.dropTimes.get(world);
				if (player != null)
					MeteorLoot.getInstance().sendMessage(player, TimeHandler.formatMsg(Settings.getInstance().checkTimeMsg, time).replaceAll("@WORLD", world.getName()));
				else
					sender.sendMessage(TimeHandler.formatMsg(Settings.getInstance().checkTimeMsg, time).replaceAll("@WORLD", world.getName()));
				return true;
			}

			if (args[1].equalsIgnoreCase("Set")) {
				if (args.length < 3 || args.length > 4) {
					if (player != null)
						MeteorLoot.getInstance().sendMessage(player, ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.DARK_GRAY + "Time " + ChatColor.GRAY + " Set <World> <Time>");
					else
						sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.DARK_GRAY + "Time " + ChatColor.GRAY + " Set <World> <Time>");
					return true;
				}

				int time = (args.length == 4 ? Integer.parseInt(args[3]) : Integer.parseInt(args[2]));
				TimeHandler.dropTimes.put(world, time);

				if (player != null)
					MeteorLoot.getInstance().sendMessage(player, TimeHandler.formatMsg(Settings.getInstance().setTimeMsg, time).replaceAll("@WORLD", world.getName()));
				else
					sender.sendMessage(TimeHandler.formatMsg(Settings.getInstance().setTimeMsg, time).replaceAll("@WORLD", world.getName()));
				return true;
			}
		}

		if (player != null)
			MeteorLoot.getInstance().sendMessage(player, ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.DARK_GRAY + "Time " + ChatColor.GRAY + " <Check/Set> " + ChatColor.DARK_GRAY + "[World]");
		else
			sender.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_PURPLE + "MeteorLoot " + ChatColor.WHITE + "Time " + ChatColor.DARK_GRAY + " <Check/Set> " + ChatColor.DARK_GRAY + "[World]");
		return true;
	}

	private static int getRandomTime() {
		Random r = new Random();
		int minTime = Settings.getInstance().minTime;
		int maxTime = Settings.getInstance().maxTime;
		return (minTime == maxTime ? minTime : maxTime - minTime < 1 ? 1 : r.nextInt(maxTime - minTime) + minTime);
	}
}
