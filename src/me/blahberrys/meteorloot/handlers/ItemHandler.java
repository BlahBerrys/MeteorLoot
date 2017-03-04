package me.blahberrys.meteorloot.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHandler implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onUseCurrencyItem(PlayerInteractEvent event) {
		if (MeteorLoot.getInstance().econ == null)
			return;
		Player player = event.getPlayer();
		ItemStack currency = player.getItemInHand();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (currency != null && currency.getType() == Settings.getInstance().currencyItem.getType()) {
				if (currency.getItemMeta() == null || currency.getItemMeta().getDisplayName() == null || currency.getItemMeta().getLore() == null)
					return;
				if (!currency.getItemMeta().getDisplayName().contains(MeteorLoot.getInstance().econ.currencyNamePlural()))
					return;
				double amount = Double.valueOf(ChatColor.stripColor(currency.getItemMeta().getDisplayName().split(" ")[0]));
				MeteorLoot.getInstance().econ.depositPlayer(player.getName(), amount);
				player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.COLOURED_DUST, null, Settings.getInstance().explosionRadius);
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().claimCurrencyMsg.replaceAll("@AMOUNT", String.valueOf(amount) + " " + MeteorLoot.getInstance().econ.currencyNamePlural()));
				player.getInventory().remove(currency);
				player.updateInventory();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<ItemStack> getMeteorChestItems() {
		try {
			ArrayList<ItemStack> chestItems = new ArrayList<ItemStack>();
			List<String> items = Settings.getInstance().chestItems;

			if (items == null || items.isEmpty())
				return null;
			Random r = new Random();

			for (String itemInfo : items) {
				String[] itemSpecs = itemInfo.split(";");

				if (itemSpecs.length != 4)
					throw new Exception(" invalid item format for item: " + itemInfo);

				String itemId = itemSpecs[0];
				int minAmount = Integer.parseInt(itemSpecs[1]);
				int maxAmount = Integer.parseInt(itemSpecs[2]);

				String itemName = null;
				String itemLore = null;
				String itemEnchants = null;
				String[] info = null;

				String details = itemSpecs[3].replace("%", "");
				if (details.contains("@")) {
					info = details.split("@");
					details = details.split("@")[0];
				}

				if (info != null) {
					for (String s : info) {
						if (s.contains("NAME="))
							itemName = s.replace("NAME=", "");
						if (s.contains("LORE="))
							itemLore = s.replace("LORE=", "");
						if (s.contains("ENCHANTS="))
							itemEnchants = s.replace("ENCHANTS=", "");
					}
				}
				int probability = Integer.parseInt(details);
				int randomAmount = (minAmount == maxAmount ? minAmount : maxAmount - minAmount < 1 ? 1 : r.nextInt(maxAmount - minAmount) + minAmount);
				ItemStack newItem = null;

				if (r.nextInt(100) <= probability) {
					if (itemId.contains(":")) {
						int id = Integer.valueOf(itemId.split(":")[0]);
						newItem = new ItemStack(Material.getMaterial(id));
						newItem.setDurability(Short.valueOf(itemId.split(":")[1]));
					} else if (itemId.contains("CURRENCY")) {
						if (MeteorLoot.getInstance().econ == null)
							continue;
						newItem = Settings.getInstance().currencyItem.clone();

						ItemMeta im = newItem.getItemMeta();
						im.setDisplayName(ChatColor.GREEN + String.valueOf(randomAmount) + " " + MeteorLoot.getInstance().econ.currencyNamePlural());
						im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "right click to claim" }));
						newItem.setItemMeta(im);
						newItem.setAmount(1);
						chestItems.add(newItem);
						continue;
					} else {
						newItem = new ItemStack(Material.getMaterial(Integer.valueOf(itemId)));
					}
					newItem.setAmount(randomAmount);
					ItemMeta im = newItem.getItemMeta();

					if (itemName != null)
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
					if (itemLore != null) {
						if (itemLore.contains(",")) {
							ArrayList<String> lore = new ArrayList<String>();
							String[] il = itemInfo.split(",");
							for (String s : il)
								lore.add(ChatColor.translateAlternateColorCodes('&', s));
							im.setLore(lore);
						} else {
							im.setLore(Arrays.asList(new String[] { ChatColor.translateAlternateColorCodes('&', itemLore) }));
						}
					}

					newItem.setItemMeta(im);

					if (itemEnchants != null) {
						if (itemEnchants.contains(",")) {
							String[] e = itemEnchants.split(",");
							for (String enchantName : e) {
								String eName = enchantName.split(":")[0];
								int eAmp = Integer.valueOf(enchantName.split(":")[1]);
								newItem.addUnsafeEnchantment(Enchantment.getByName(eName), eAmp);
							}
						} else {
							String eName = itemEnchants.split(":")[0];
							int eAmp = Integer.valueOf(itemEnchants.split(":")[1]);
							newItem.addUnsafeEnchantment(Enchantment.getByName(eName), eAmp);
						}
					}
					chestItems.add(newItem);
				}
			}
			return chestItems;
		} catch (Exception ex) {
			ex.printStackTrace();
			MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "Failed to load meteor items due to error: " + ex.getMessage());
		}
		return null;
	}
}
