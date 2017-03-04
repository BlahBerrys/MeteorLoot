package me.blahberrys.meteorloot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.handlers.ChestHandler;
import me.blahberrys.meteorloot.meteor.MeteorShape;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;

/**
 * @author BlahBerrys (eballer48) - ericballard7@gmail.com
 * 
 *         SchematicAPI [Altered for MeteorLoot] An easy-to-use API for saving,
 *         loading, and pasting WorldEdit/MCEdit schematics. (Built against
 *         WorldEdit 6.1) *
 */

@SuppressWarnings("deprecation")
public class SchematicUtils {

	public static void save(Player player, String schematicName) {
		if (!MeteorLoot.getInstance().worldEditSupport) {
			MeteorLoot.getInstance().sendMessage(player, "WorldEdit: " + ChatColor.RED + String.valueOf(MeteorLoot.getInstance().worldEditSupport));
			MeteorLoot.getInstance().sendMessage(player, "WorldEdit dependency will be removed in a future update.");
			return;
		}

		try {
			File schematic = new File(MeteorLoot.getInstance().getDataFolder(), "/schematics/" + schematicName);
			if (schematic.exists()) {
				MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().meteorExistMsg.replace("@NAME", schematicName));
				return;
			}

			File dir = new File(MeteorLoot.getInstance().getDataFolder(), "/schematics/");
			if (!dir.exists())
				dir.mkdirs();

			WorldEditPlugin wep = MeteorLoot.getInstance().worldEdit;
			WorldEdit we = MeteorLoot.getInstance().worldEdit.getWorldEdit();

			LocalPlayer localPlayer = wep.wrapPlayer(player);
			LocalSession localSession = we.getSession(localPlayer);
			ClipboardHolder selection = localSession.getClipboard();
			EditSession editSession = localSession.createEditSession(localPlayer);

			Vector min = selection.getClipboard().getMinimumPoint();
			Vector max = selection.getClipboard().getMaximumPoint();
			CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);

			editSession.enableQueue();
			clipboard.copy(editSession);
			SchematicFormat.MCEDIT.save(clipboard, schematic);
			editSession.flushQueue();

			MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().createMeteorMsg.replace("@NAME", schematicName));
		} catch (IOException | DataException ex) {
			ex.printStackTrace();
			MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().errorMsg);
		} catch (EmptyClipboardException ex) {
			MeteorLoot.getInstance().sendMessage(player, Settings.getInstance().noSelectionMsg);
		}
	}

	public static CuboidClipboard load(MeteorShape shape) {
		try {
			File dir = new File(MeteorLoot.getInstance().getDataFolder(), "/schematics/" + shape.getShapeName());
			SchematicFormat schematic = SchematicFormat.getFormat(dir);
			return schematic.load(dir);
		} catch (DataException | IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void paste(MeteorShape shape, Location pasteLoc) {
		try {
			Schematic schematic = SchematicUtils.loadSchematic(new File(MeteorLoot.getInstance().getDataFolder(), "/schematics/" + shape.getShapeName()));
			pasteLoc = pasteLoc.clone().subtract(schematic.getWidth() / 2, schematic.getHeight() / 2, schematic.getLength() / 2);

			EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
			editSession.enableQueue();

			CuboidClipboard clipboard = load(shape);
			clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc.clone()), true, true);
			editSession.flushQueue();

			ChestHandler.fillChest(schematic, pasteLoc.clone());
		} catch (MaxChangedBlocksException ex) {
			ex.printStackTrace();
		}
	}

	public static Material getMaterial(int id) {
		if (Material.getMaterial(id) == null)
			return Material.AIR;
		else
			return Material.getMaterial(id);
	}

	public static Schematic loadSchematic(File file) {
		try {
			if (file.exists()) {
				NBTInputStream nbtStream = new NBTInputStream(new FileInputStream(file));
				CompoundTag compound = (CompoundTag) nbtStream.readTag();
				Map<String, Tag> tags = compound.getValue();

				Short width = ((ShortTag) tags.get("Width")).getValue();
				Short height = ((ShortTag) tags.get("Height")).getValue();
				Short length = ((ShortTag) tags.get("Length")).getValue();

				String materials = ((StringTag) tags.get("Materials")).getValue();

				byte[] blocksId = ((ByteArrayTag) tags.get("Blocks")).getValue();

				byte[] data = ((ByteArrayTag) tags.get("Data")).getValue();

				// ive found this at the github of the makers of worldedit so
				// credits to them, it looks I was doing it wrong by using
				// byte[] at the first place
				// and the datavalues are hex while a byte is not hex but a
				// short can accept hex as 16, now the only thing to learn is
				// the Tag AddBlocks and why it is used.
				short[] blocks = new short[blocksId.length];

				// need to look a bit over this.
				byte[] addId = new byte[0];
				if (tags.containsKey("AddBlocks")) {
					addId = ((ByteArrayTag) tags.get("AddBlocks")).getValue();
				}

				// Combine the AddBlocks data with the first 8-bit block ID
				for (int index = 0; index < blocksId.length; index++) {
					if ((index >> 1) >= addId.length) { // No corresponding
														// AddBlocks index
						blocks[index] = (short) (blocksId[index] & 0xFF);
					} else {
						if ((index & 1) == 0) {
							blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blocksId[index] & 0xFF));
						} else {
							blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blocksId[index] & 0xFF));
						}
					}
				}
				// end of worldedit snippet.

				nbtStream.close();

				return new Schematic(file.getName().replace(".schematic", ""), width, height, length, materials, blocks, data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
