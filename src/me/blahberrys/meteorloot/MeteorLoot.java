package me.blahberrys.meteorloot;

import java.io.IOException;

import me.blahberrys.meteorloot.commands.Commands;
import me.blahberrys.meteorloot.handlers.ItemHandler;
import me.blahberrys.meteorloot.handlers.MeteorHandler;
import me.blahberrys.meteorloot.handlers.SelectionHandler;
import me.blahberrys.meteorloot.handlers.TimeHandler;
import me.blahberrys.meteorloot.managers.factions.FactionsManager;
import me.blahberrys.meteorloot.meteor.Meteor;
import me.blahberrys.meteorloot.utils.Metrics;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MeteorLoot extends JavaPlugin {

	public static MeteorLoot instance;

	public static MeteorLoot getInstance() {
		return instance;
	}

	// Dependencies
	public Boolean vaultSupport = false;
	public Boolean townySupport = false;
	public Boolean factionsSupport = false;
	public Boolean worldGuardSupport = false;
	public Boolean worldEditSupport = false;
	public Boolean griefPreventionSupport = false;

	public WorldEditPlugin worldEdit = null;
	public FactionsManager factionsManager = null;
	public Economy econ = null;

	public void broadcastMessage(World world, String message) {
		if (Settings.getInstance().worlds.size() == 1)
			getServer().broadcastMessage(Settings.getInstance().title + message);
		else
			for (Player player : world.getPlayers())
				player.sendMessage(Settings.getInstance().title + message);
	}

	public void sendMessage(Player player, String message) {
		player.sendMessage(Settings.getInstance().title + message);
	}

	@Override
	public void onEnable() {
		instance = this;
		setupPlugin();
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "has been enabled!");
	}

	@Override
	public void onDisable() {
		TimeHandler.stopTimeHandler();
		Data.save();
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "has been disabled.");

		Settings.instance = null;
		Meteor.instance = null;
		instance = null;
	}

	private void setupPlugin() {
		Meteor.getInstance().patchMeteor();
		Settings.getInstance().loadSettings();
		Data.load();

		new Commands();
		Commands cmd = new Commands();
		getCommand("meteorloot").setExecutor(cmd);
		getServer().getPluginManager().registerEvents(new MeteorHandler(), this);
		getServer().getPluginManager().registerEvents(new SelectionHandler(), this);
		getServer().getPluginManager().registerEvents(new ItemHandler(), this);

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}

		setupTowny();
		setupFactions();
		setupWorldEdit();
		setupWorldGuard();
		setupGriefPrevention();
		setupVault();

		TimeHandler.setNextDropTimes();
		TimeHandler.startTimeHandler();
	}

	private void setupFactions() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Factions");
		if (plugin == null)
			return;

		factionsSupport = true;
		factionsManager = new FactionsManager(this, plugin);
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "enabled Factions " + factionsManager.getFactions().getVersion().toString() + " support!");
	}

	private void setupTowny() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Towny");
		if (plugin == null)
			return;

		townySupport = true;
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "enabled Towny support!");
	}

	private void setupGriefPrevention() {
		Plugin plugin = getServer().getPluginManager().getPlugin("GriefPrevention");
		if (plugin == null)
			return;

		griefPreventionSupport = true;
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "enabled GriefPrevention support!");
	}

	private void setupWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin))
			return;

		worldGuardSupport = true;
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "enabled WorldGuard support!");
	}

	private void setupWorldEdit() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin))
			return;

		worldEdit = (WorldEditPlugin) plugin;
		worldEditSupport = true;
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "enabled WorldEdit support!");
	}

	private void setupVault() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if (plugin == null)
			return;
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			econ = economyProvider.getProvider();

		vaultSupport = true;
		getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "enabled Vault support!");
	}
}
