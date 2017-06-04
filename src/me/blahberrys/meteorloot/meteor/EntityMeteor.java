package me.blahberrys.meteorloot.meteor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.handlers.BlockHandler;
import me.blahberrys.meteorloot.handlers.MeteorHandler;
import me.blahberrys.meteorloot.utils.Selection;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.World;

public class EntityMeteor extends EntityLargeFireball {

	public final MeteorShape shape;
	public final Boolean forceSpawned;

	private boolean h_lock;
	private boolean h_lock_2;
	private boolean h_lock_3;

	public EntityMeteor(World world, Boolean forceSpawned, MeteorShape shape) {
		super(world);
		this.forceSpawned = forceSpawned;
		this.shape = shape;
	}

	public void spawn(Location spawnLoc) {
		spawnLoc.getChunk();
		MeteorHandler.meteors.add(this.getUniqueID());
		this.shooter = new EntityEnderDragon(this.world);
		this.world.addEntity(this, SpawnReason.NATURAL);
		this.setPosition(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
	}

	@Override
	public void h() {
		move();
		super.h();
	}
	
	/*
	@Override
	public MinecraftServer h() {
		move();
		return super.h();
	}
	
	 *
	 @Override
	public void h() {
		move();
		super.h();
	}

	 */

	private void move() {
		this.h_lock = (!this.h_lock);
		if (!this.h_lock) {
			this.h_lock_2 = (!this.h_lock_2);
			if (!this.h_lock_2) {
				this.h_lock_3 = (!this.h_lock_3);
				if (!this.h_lock_3) {
					int locY = (int) this.locY;
					if ((locY & 0xFFFFFF00) != 0) {
						this.dead = true;
						return;
					}

					if ((locY & 0xFFFFFFE0) == 0) {

						try {
							explode();
						} catch (NullPointerException ignored) {
							ignored.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}

					this.world.createExplosion(this, this.locX, this.locY, this.locZ, Settings.getInstance().explosionRadius, true, true);
				}
			}
		}
		this.motX *= 1.0D;
		this.motY *= 1.0D;
		this.motZ *= 1.0D;
	}

	@Override
	public void a(MovingObjectPosition movingobjectposition) {
		explode();
	}

	private void dropOre(Location loc, Integer radius, Material ore) {
		Random r = new Random();
		loc.getWorld().dropItemNaturally(loc.add(r.nextInt(radius), loc.getWorld().getHighestBlockYAt(loc) - loc.getBlockY() + 5, r.nextInt(radius)), new ItemStack(ore, r.nextInt(5)));
	}

	private void explode() {
		this.die();
		this.world.removeEntity(this);

		int radius = Math.round(Settings.getInstance().explosionRadius);
		int mRadius = Settings.getInstance().meteorRadius;
		Location l = world.getWorld().getHighestBlockAt((int) this.locX, (int) this.locZ).getLocation().clone();

		// Burrow meteor
		if (Settings.getInstance().burrowMultiplier > 0)
			for (int i = 0; i < Settings.getInstance().burrowMultiplier; i++) {
				int r = (radius - i > mRadius ? radius - i : mRadius);

				l = l.clone().subtract(0, mRadius, 0);
				createExplosion(l, r, this.shape);

				/*
				 * [1.7 METHOD] l = l.clone().subtract(0, Math.round((i *
				 * radius) / mRadius), 0); createExplosion(l, ((r & 1) == 0 ? r
				 * + 1 : r));
				 */
			}
		else
			createExplosion(l.clone(), radius, this.shape);

		// Spawn meteor blocks
		Meteor.getInstance().createAftermath(l.clone(), this.forceSpawned, this.shape);
	}

	private void createExplosion(Location location, int radius, MeteorShape shape) {
		Random r = new Random();
		HashMap<Location, Material> data = new HashMap<Location, Material>();
		List<Material> ores = Arrays.asList(Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE);
		List<Material> notBlocks = Arrays.asList(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);

		/*
		 * // Determine Explosion Shape Iterator<Location> iterator = null; if
		 * (shape.getShape() == MeteorShape.Shape.SPHERE) { iterator =
		 * Selection.getCircle(location.clone(), radius, false,
		 * true).iterator(); } else if (shape.getShape() ==
		 * MeteorShape.Shape.CUBE) { iterator =
		 * Selection.getCube(location.clone(), radius).iterator();
		 * 
		 * } else if (shape.getShape() == MeteorShape.Shape.SCHEMATIC) { //
		 * CuboidClipboard schematic = Schematic.load(shape); //
		 * LinkedHashMap<Block, Integer> dick = new LinkedHashMap<Block, //
		 * Integer>(); location = location.clone().add(0,
		 * (Settings.getInstance().meteorRadius / 2), 0); Schematic schematic =
		 * SchematicUtils.loadSchematic(new
		 * File(MeteorLoot.getInstance().getDataFolder(), "/schematics/" +
		 * shape.getShapeName()));
		 * 
		 * for (int y = 0; y < schematic.getHeight(); y++) { for (int x = 0; x <
		 * schematic.getWidth(); x++) { for (int z = 0; z <
		 * schematic.getLength(); ++z) {
		 * 
		 * Location temp = location.clone().add(x, y, z); Block block =
		 * temp.getBlock();
		 * 
		 * //int index = y * schematic.getWidth() * schematic.getLength() + z *
		 * schematic.getWidth() + x; if
		 * (getMaterial(schematic.getBlocks()[index]) != Material.AIR) {
		 * block.setType(Material.AIR); } } } } return;
		 */

		for (Iterator<Location> nearBlocks = Selection.getCircle(location.clone(), radius, false, true).iterator(); nearBlocks.hasNext();) {
			Location l = nearBlocks.next();
			Block b = l.getBlock();

			data.put(l.clone(), l.getBlock().getType());
			if (!notBlocks.contains(b.getType())) {
				if (Settings.getInstance().oresToIngots && ores.contains(b.getType()))
					dropOre(l.clone(), radius, b.getType());
				if (r.nextInt(100) <= Settings.getInstance().dropChance)
					b.breakNaturally();
				else if (r.nextInt(100) <= Settings.getInstance().fireChance)
					b.setType(Material.FIRE);
				else
					b.setType(Material.AIR);
			}
		}
		BlockHandler.addBlocksToRepair(location.getWorld(), data, false);
	}
}