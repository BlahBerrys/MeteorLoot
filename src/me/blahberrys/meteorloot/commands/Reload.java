package me.blahberrys.meteorloot.commands;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.utils.ConfigManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload {

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandException {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;

			if (!(player.hasPermission("meteorloot.reload"))) {
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().noPermMsg);
				return true;
			}
		}

		ConfigManager.reload(MeteorLoot.getInstance(), "config.yml");
		ConfigManager.reload(MeteorLoot.getInstance(), "lang_de.yml");
		ConfigManager.reload(MeteorLoot.getInstance(), "lang_en.yml");
		ConfigManager.reload(MeteorLoot.getInstance(), "lang_es.yml");
		ConfigManager.reload(MeteorLoot.getInstance(), "lang_fr.yml");
		Settings.getInstance().loadSettings();

		if (player != null)
			MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().reloadMsg);
		else
			sender.sendMessage(Settings.getInstance().reloadMsg);
		return true;
	}

}
