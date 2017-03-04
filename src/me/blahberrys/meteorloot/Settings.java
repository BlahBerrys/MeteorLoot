package me.blahberrys.meteorloot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import me.blahberrys.meteorloot.utils.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Settings {

	public static Settings instance = new Settings();

	public static Settings getInstance() {
		return instance;
	}

	// Plugin Settings
	public Integer configVersion = 0;
	public Integer langVersion = 0;
	public Integer dataVersion = 0;

	public List<World> worlds = new ArrayList<World>();
	public List<String> activeCycle = new ArrayList<String>();

	public List<String> schematics = new ArrayList<String>();
	public ItemStack selectorWand = null;
	public ItemStack currencyItem = null;

	public ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();
	public ArrayList<ItemStack> barrier = new ArrayList<ItemStack>();
	/*
	 * In corresponding order with config.yml
	 */

	public Integer saveInterval = 0;
	public String title = "";
	public String lang = "";
	public String selectorID = "";
	public String warzoneName = "";
	public String safezoneName = "";
	public List<String> enabledWorlds = new ArrayList<String>();

	// Meteor Settings
	public Integer meteorRadius = 0;
	public Boolean hollow = false;
	public Boolean canMine = false;
	public List<String> meteorBlocks = new ArrayList<String>();
	public List<String> barrierBlocks = new ArrayList<String>();

	// Spawn Settings
	public Integer spawnHeight = 0;
	public Integer minTime = 0;
	public Integer maxTime = 0;
	public Integer minBlocksFromSpawn = 0;
	public Integer maxBlocksFromSpawn = 0;
	public List<String> shapeCycle = new ArrayList<String>();

	// Impact Settings
	public Integer timeTilRepair = 0;
	public Integer burrowMultiplier = 0;
	public Boolean showTimeBar = false;
	public Boolean broadcastNext = false;
	public String warnIntervals = "";
	public List<String> effects = new ArrayList<String>();
	public List<String> soundEffects = new ArrayList<String>();

	// Explosion Settings
	public Integer explosionRadius = 0;
	public Integer fireChance = 0;
	public Integer dropChance = 0;
	public Boolean oresToIngots = false;

	// Loot Settings
	public Integer minItems = 0;
	public Integer maxItems = 0;
	public List<String> chestItems = new ArrayList<String>();
	public String currency = "";

	/*
	 * In corresponding order with lang_.yml
	 */

	// Plugin Messages
	public String errorMsg = "";
	public String repairMsg = "";
	public String reloadMsg = "";
	public String inProgMsg = "";
	public String noPermMsg = "";
	public String notEnabledMsg = "";
	public String checkTimeMsg = "";
	public String setTimeMsg = "";
	public String createMeteorMsg = "";
	public String meteorExistMsg = "";
	public String spawnMeteorMsg = "";
	public String claimCurrencyMsg = "";

	// Meteor Messages
	public String chestTitle = "";
	public String landingMsg = "";
	public String broadcastNextMsg = "";
	public String firstLootMsg = "";

	// Selector Messages
	public String pointAMsg = "";
	public String pointBMsg = "";
	public String selectorDesc = "";
	public String diffWorldsMsg = "";
	public String samePointsMsg = "";
	public String noSelectionMsg = "";

	/*
	 * End Settings
	 */

	public String getMessage(String message, String def) {
		return ChatColor.translateAlternateColorCodes('&', ConfigManager.get(lang).getString(message, def));
	}

	// Gets a String from selected lang file & translates ChatColors
	public String getMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', ConfigManager.get(lang).getString(message));
	}

	public void loadSettings() {
		// mkdirs()
		ConfigManager.load(MeteorLoot.getInstance(), "config.yml");
		ConfigManager.load(MeteorLoot.getInstance(), "lang_en.yml");
		ConfigManager.load(MeteorLoot.getInstance(), "lang_es.yml");
		ConfigManager.load(MeteorLoot.getInstance(), "lang_de.yml");
		ConfigManager.load(MeteorLoot.getInstance(), "lang_fr.yml");

		ConfigurationSection config = ConfigManager.get("config.yml");
		configVersion = config.getInt("version", 1);
		title = ChatColor.translateAlternateColorCodes('&', config.getString("PLUGIN.header", "&5[&8MeteorLoot&5]&7")) + " ";

		tryUpdate(config, configVersion, title);

		// Get Plugin Settings
		saveInterval = config.getInt("PLUGIN.saveInterval", 5);
		lang = config.getString("PLUGIN.lang", "lang_en.yml");
		selectorID = config.getString("PLUGIN.selectorID", "392");
		enabledWorlds = config.getStringList("PLUGIN.enabledWorlds");
		warzoneName = config.getString("PLUGIN.FACTIONS.warzone");
		safezoneName = config.getString("PLUGIN.FACTIONS.safezone");

		// Get Meteor Settings
		meteorRadius = config.getInt("METEOR.radius", 4);
		hollow = config.getBoolean("METEOR.hollow", false);
		canMine = config.getBoolean("METEOR.canMine", false);
		meteorBlocks = config.getStringList("METEOR.blocks");
		barrierBlocks = config.getStringList("METEOR.barrierBlocks");

		// Get Spawn Settings
		spawnHeight = config.getInt("METEOR.SPAWN.height", 200);
		minTime = config.getInt("METEOR.SPAWN.minTime", 15);
		maxTime = config.getInt("METEOR.SPAWN.maxTime", 45);
		minBlocksFromSpawn = config.getInt("METEOR.SPAWN.minBlocksFromSpawn", 400);
		maxBlocksFromSpawn = config.getInt("METEOR.SPAWN.maxBlocksFromSpawn", 2500);
		shapeCycle = config.getStringList("METEOR.SPAWN.shapeCycle");

		// Get Impact Settings
		timeTilRepair = config.getInt("METEOR.IMPACT.timeTilRepair", 20);
		burrowMultiplier = config.getInt("METEOR.IMPACT.burrowMultiplier", 5);
		showTimeBar = config.getBoolean("METEOR.IMPACT.showTimeBar", true);
		broadcastNext = config.getBoolean("METEOR.IMPACT.broadcastNext", true);
		warnIntervals = config.getString("METEOR.IMPACT.warnIntervals", "45,30,20,10,5,1");
		effects = config.getStringList("METEOR.IMPACT.vfx");
		soundEffects = config.getStringList("METEOR.IMPACT.sfx");

		// Get Explosion Settings
		explosionRadius = config.getInt("METEOR.IMPACT.radius", 8);
		fireChance = config.getInt("METEOR.IMPACT.fireChance", 20);
		dropChance = config.getInt("METEOR.IMPACT.blockDropChance", 30);
		oresToIngots = config.getBoolean("METEOR.IMPACT.oresToIngots", true);

		// Get Loot Settings
		minItems = config.getInt("METEOR.LOOT.minItems", 5);
		maxItems = config.getInt("METEOR.LOOT.maxItems", 25);
		chestItems = config.getStringList("METEOR.LOOT.items");
		currency = config.getString("METEOR.LOOT.currencyID", "339");

		/*
		 * Get Messages
		 */
		MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "language: " + lang);

		// Plugin Messages
		errorMsg = getMessage("PLUGIN.error");
		repairMsg = getMessage("PLUGIN.repair");
		reloadMsg = getMessage("PLUGIN.reload");
		inProgMsg = getMessage("PLUGIN.inProgress");
		noPermMsg = getMessage("PLUGIN.noPermission");
		notEnabledMsg = getMessage("PLUGIN.notEnabled");
		checkTimeMsg = getMessage("PLUGIN.checkTime");
		setTimeMsg = getMessage("PLUGIN.setTime");
		createMeteorMsg = getMessage("PLUGIN.createMeteor");
		meteorExistMsg = getMessage("PLUGIN.meteorExist");
		spawnMeteorMsg = getMessage("PLUGIN.spawnMeteor");
		claimCurrencyMsg = getMessage("PLUGIN.claimCurrency");

		// Meteor Messages
		chestTitle = getMessage("METEOR.chestName");
		landingMsg = getMessage("METEOR.impact");
		broadcastNextMsg = getMessage("METEOR.broadcastNext");
		firstLootMsg = getMessage("METEOR.firstLoot");

		// Selector Messages
		pointAMsg = getMessage("SELECTOR.pointA");
		pointBMsg = getMessage("SELECTOR.pointB");
		selectorDesc = getMessage("SELECTOR.description");
		diffWorldsMsg = getMessage("SELECTOR.diffWorlds");
		samePointsMsg = getMessage("SELECTOR.samePoints");
		noSelectionMsg = getMessage("SELECTOR.noSelection");

		setupCurrencyItem();
		setupSelector();
		registerWorlds();
		registerShapeCycle();
		registerMeteorBlocks();
	}

	private void tryUpdate(ConfigurationSection config, Integer configVersion, String header) {
		if (configVersion == 1) {
			config.set("PLUGIN.FACTIONS.warzone", "warzone");
			config.set("PLUGIN.FACTIONS.safezone", "safezone");
			config.set("METEOR.IMPACT.showTimeBar", true);
			config.set("version", 2);
			ConfigManager.save(MeteorLoot.getInstance(), "config.yml");
			MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(header + "updated config to v2!");
		}
	}

	@SuppressWarnings("deprecation")
	private void registerMeteorBlocks() {
		for (String s : meteorBlocks) {
			ItemStack i = null;
			if (s.contains(":")) {
				int id = Integer.valueOf(s.split(":")[0]);
				i = new ItemStack(Material.getMaterial(id));
				i.setDurability(Short.valueOf(s.split(":")[1]));
			} else {
				i = new ItemStack(Material.getMaterial(Integer.valueOf(s)));
			}
			if (i != null)
				blocks.add(i);
		}

		for (String s : barrierBlocks) {
			ItemStack i = null;
			if (s.contains(":")) {
				int id = Integer.valueOf(s.split(":")[0]);
				i = new ItemStack(Material.getMaterial(id));
				i.setDurability(Short.valueOf(s.split(":")[1]));
			} else {
				i = new ItemStack(Material.getMaterial(Integer.valueOf(s)));
			}
			barrier.add(i);
		}
	}

	private void registerWorlds() {
		if (enabledWorlds.isEmpty())
			Bukkit.getServer().getConsoleSender().sendMessage(title + "failed to register enabledWorlds, please check config.");
		for (String worldName : enabledWorlds) {
			if (Bukkit.getServer().getWorld(worldName) != null) {
				worlds.add(Bukkit.getServer().getWorld(worldName));
			} else {
				Bukkit.getServer().getConsoleSender().sendMessage(title + "failed to load world '" + worldName + "'.. does it exist?");
			}
		}
		if (worlds.isEmpty()) {
			Bukkit.getServer().getConsoleSender().sendMessage(title + "failed to find a valid world, disabling plugin..");
			Bukkit.getServer().getPluginManager().disablePlugin(MeteorLoot.getInstance());
		}
	}

	public void registerShapeCycle() {
		List<String> cycle = new ArrayList<String>();

		for (String shape : shapeCycle) {
			List<String> shapes = Arrays.asList(new String[] { "SPHERE", "CUBE", "POLYGON", "RANDOM" });

			if (!shapes.contains(shape)) {
				if (!(new File(MeteorLoot.getInstance().getDataFolder() + "/schematics/", shape).exists()))
					Bukkit.getServer().getConsoleSender().sendMessage(title + "invalid shape: " + shape);
				else
					cycle.add(shape);
			} else {
				cycle.add(shape);
			}
		}

		if (cycle.contains("RANDOM")) {
			cycle.remove("RANDOM");

			Iterator<String> iterator = cycle.iterator();
			while (iterator.hasNext()) {
				Random random = new Random();
				String rShape = cycle.get(random.nextInt(cycle.size()));
				activeCycle.add(rShape);
				cycle.remove(rShape);
			}
			MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "shapeCycle has been set to random.");
		} else {
			activeCycle = cycle;
		}
	}

	@SuppressWarnings("deprecation")
	private void setupCurrencyItem() {
		if (currency.contains(":")) {
			currencyItem = new ItemStack(Material.getMaterial(Integer.valueOf(Settings.getInstance().currency.split(":")[0])));
			currencyItem.setDurability(Short.valueOf(Settings.getInstance().currency.split(":")[1]));
		} else {
			currencyItem = new ItemStack(Material.getMaterial(Integer.valueOf(Settings.getInstance().currency)));
		}
	}

	@SuppressWarnings("deprecation")
	private void setupSelector() {
		ItemStack selector = null;

		if (selectorID.contains(":")) {
			selector = new ItemStack(Material.getMaterial(Integer.valueOf(selectorID.split(":")[0])));
			selector.setDurability(Short.valueOf(selectorID.split(":")[1]));
		} else {
			selector = new ItemStack(Material.getMaterial(Integer.valueOf(selectorID)));
		}

		ItemMeta im = selector.getItemMeta();
		im.setDisplayName(title + " Selector");
		im.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', selectorDesc).split(",")));
		selector.setItemMeta(im);
		selectorWand = selector;
	}
}
