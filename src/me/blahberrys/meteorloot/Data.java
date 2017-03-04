package me.blahberrys.meteorloot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import me.blahberrys.meteorloot.meteor.Meteor;
import me.blahberrys.meteorloot.utils.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Data {

	public static void save() {
		File dataFile = new File(MeteorLoot.getInstance().getDataFolder(), "data.bin");

		try {
			if (!dataFile.exists()) {
				MeteorLoot.getInstance().getDataFolder().mkdirs();
				dataFile.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));
			oos.writeInt(1);
			oos.writeObject(serializeBlockData());
			oos.flush();
			oos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "failed to save data.");
			return;
		}
		MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "saved block data!");
	}

	@SuppressWarnings("unchecked")
	public static void load() {
		File dataFile = new File(MeteorLoot.getInstance().getDataFolder(), "data.bin");

		if (dataFile.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile));
				Settings.getInstance().dataVersion = ois.readInt();
				Meteor.getInstance().blocksToRepair = deserializeBlockData((HashMap<String, HashMap<String, String>>) ois.readObject());
				ois.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "Failed to load previous block data.");
				dataFile.delete();
				return;
			}
		}
		MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "loaded block data!");
	}

	private static HashMap<String, HashMap<String, String>> serializeBlockData() {
		HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>();

		for (Iterator<World> iterator = Meteor.getInstance().blocksToRepair.keySet().iterator(); iterator.hasNext();) {
			World w = iterator.next();
			if (!data.containsKey(w.getName()))
				data.put(w.getName(), new HashMap<String, String>());
			for (Location l : Meteor.getInstance().blocksToRepair.get(w).keySet())
				data.get(w.getName()).put(LocationUtil.serialization.blockLocToString(l), Meteor.getInstance().blocksToRepair.get(w).get(l).name());
		}
		return data;
	}

	private static HashMap<World, HashMap<Location, Material>> deserializeBlockData(HashMap<String, HashMap<String, String>> hashMap) {
		HashMap<World, HashMap<Location, Material>> data = new HashMap<World, HashMap<Location, Material>>();

		for (String worldName : hashMap.keySet()) {
			World w = Bukkit.getServer().getWorld(worldName);

			if (!data.containsKey(w))
				data.put(w, new HashMap<Location, Material>());

			for (String l : hashMap.get(worldName).keySet())
				data.get(w).put(LocationUtil.serialization.blockStringToLoc(l), Material.valueOf(hashMap.get(worldName).get(l)));
		}
		return data;
	}
}
