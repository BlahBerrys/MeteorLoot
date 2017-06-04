package me.blahberrys.meteorloot.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.meteor.Meteor;
import me.blahberrys.meteorloot.utils.Schematic;
import me.blahberrys.meteorloot.utils.SchematicUtils;
import net.minecraft.server.v1_8_R3.TileEntityChest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class ChestHandler {

	public static void fillChest(Schematic schematic, Location pasteLoc) {
		HashMap<Location, Material> data = new HashMap<Location, Material>();
		if (!Meteor.getInstance().meteorBlocks.containsKey(pasteLoc.getWorld()))
			Meteor.getInstance().meteorBlocks.put(pasteLoc.getWorld(), new ArrayList<Location>());

		for (int y = 0; y < schematic.getHeight(); y++) {
			for (int x = 0; x < schematic.getWidth(); x++) {
				for (int z = 0; z < schematic.getLength(); ++z) {
					Location temp = pasteLoc.clone().add(x, y, z);
					data.put(temp.clone(), temp.getBlock().getType());

					int index = y * schematic.getWidth() * schematic.getLength() + z * schematic.getWidth() + x;
					if (SchematicUtils.getMaterial(schematic.getBlocks()[index]) != Material.AIR)
						Meteor.getInstance().meteorBlocks.get(pasteLoc.getWorld()).add(temp.clone());
					if (SchematicUtils.getMaterial(schematic.getBlocks()[index]) == Material.CHEST)
						dropLoot(temp.clone());
				}
			}
			BlockHandler.addBlocksToRepair(pasteLoc.getWorld(), data, false);
		}
	}

	public static void dropLoot(Location loc) {
		try {
			ArrayList<ItemStack> configItems = ItemHandler.getMeteorChestItems();
			if (configItems == null || configItems.isEmpty())
				throw new Exception("Failed to loads chest items.");

			Random r = new Random();
			int minItems = Settings.getInstance().minItems;
			int maxItems = Settings.getInstance().maxItems;
			int toAdd = r.nextInt(maxItems);
			int itemsToAdd = (maxItems == minItems ? minItems : maxItems - minItems < 1 ? 1 : toAdd >= minItems ? toAdd : minItems);

			if (loc.getBlock().getType() != Material.CHEST)
				loc.getBlock().setType(Material.CHEST);
			Chest c = (Chest) loc.getBlock().getState();
			Field inventoryField = c.getClass().getDeclaredField("chest");
			inventoryField.setAccessible(true);
			TileEntityChest teChest = ((TileEntityChest) inventoryField.get(c));
			teChest.a(Settings.getInstance().chestTitle);

			for (int itemsAdded = 0; itemsAdded < itemsToAdd; itemsAdded++) {
				// Gets empty slot in chest
				int chestSlot = r.nextInt(c.getBlockInventory().getSize());
				while (c.getBlockInventory().getItem(chestSlot) != null)
					chestSlot = r.nextInt(c.getBlockInventory().getSize());

				// Gets random item from config 'items'
				int ranChoice = r.nextInt(configItems.size());
				int itemChoice = (ranChoice < configItems.size() ? ranChoice : 0);

				ItemStack randomItem = configItems.get(itemChoice);
				configItems.remove(itemChoice);
				if (configItems.isEmpty())
					configItems = ItemHandler.getMeteorChestItems();
				c.getBlockInventory().setItem(chestSlot, randomItem);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "Failed to drop loot due to error: " + ex.getMessage());
		}
	}
}
