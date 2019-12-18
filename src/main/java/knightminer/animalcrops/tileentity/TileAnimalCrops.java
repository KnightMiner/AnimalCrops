package knightminer.animalcrops.tileentity;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class TileAnimalCrops extends TileEntity {
	/** Tag to use to store the random direction */
	public static final String TAG_DIRECTION = "direction";

	/**
	 * Cached entity from {@link #getEntity(boolean)}.
	 * Typically null except on the client side (only cached during fancy crop rendering)
	 * */
	private MobEntity entity;

	public TileAnimalCrops() {
		super(Registration.cropsTE);
	}


	/* Getters and setters */

	/**
	 * Sets the entity into the TE
	 * @param entityID  Entity ID to set
	 */
	public void setEntity(String entityID) {
		if(world.isRemote || entityID == null) {
			return;
		}

		// only set if valid
		if (!entityValid(entityID)) {
			return;
		}

		CompoundNBT data = this.getTileData();
		data.putString(Utils.ENTITY_TAG, entityID);
		data.putInt(TAG_DIRECTION, world.rand.nextInt(4));
		this.markDirty();
	}

	/**
	 * Gets the entity for this crop
	 * @param  cacheEntity  If true, pull the entity from the cache and cache the result. Typically only used clientside
	 * @return  The entity for this crop
	 */
	public MobEntity getEntity(boolean cacheEntity) {
		if (cacheEntity && entity != null) {
			return entity;
		}

		// create the entity from the tile data
		Entity created = Utils.getEntityID(this.getTileData())
													.filter(TileAnimalCrops::entityValid)
													.flatMap(EntityType::byKey)
													.map((type) -> type.create(world))
													.orElse(null);
		if (created == null) {
			return null;
		}

		// if the entity is not MobEntity, discard it
		if (!(created instanceof MobEntity)) {
			created.remove();
			return null;
		}

		// set the age for ageable entities
		MobEntity entity = (MobEntity)created;
		entity.prevRotationYaw = entity.rotationYaw = this.getAngle();
		entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
		entity.prevRenderYawOffset = entity.renderYawOffset = entity.rotationYaw;

		if(entity instanceof AgeableEntity) {
			((AgeableEntity)entity).setGrowingAge(-24000);
		}

		// cache the entity if requested
		if (cacheEntity) {
			this.entity = entity;
		}
		return entity;
	}

	/**
	 * Gets the angle for the given random crop direction
	 * @return  Angle in 90 degree increments
	 */
	public float getAngle() {
		int index = this.getTileData().getInt(TAG_DIRECTION);
		if (index < 0 || index > 3) {
			return Direction.SOUTH.getHorizontalAngle();
		}
		return Direction.byHorizontalIndex(index).getHorizontalAngle();
	}

	/**
	 * Spawns the current entity into the world then clears it
	 */
	public void spawnAnimal() {
		// if we have no entity, give up
		MobEntity entity = getEntity(false);
		if(entity == null) {
			return;
		}

		// set position
		entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

		// set entity data where relevant
		entity.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entity)), SpawnReason.SPAWN_EGG, null, null);

		// slime sizes should not be bigger than 2
		if(entity instanceof SlimeEntity) {
			SlimeEntity slime = (SlimeEntity)entity;
			if(slime.getSlimeSize() > 2) {
				Utils.setSlimeSize(slime, 2);
			}
		}

		// spawn
		entity.setWorld(world);
		world.addEntity(entity);
		entity.playAmbientSound();
	}


	/* NBT */

	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		// new tag instead of super since default implementation calls the private writeInternal
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		// only the data in the forge tile data tag needs to be sent over the network
		CompoundNBT tag = this.getTileData().copy();
		return new SUpdateTileEntityPacket(this.getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		// clone all of of the data into the forge tile data
		CompoundNBT tag = pkt.getNbtCompound();
		this.getTileData().merge(tag);
	}


	/* Helpers */

	/**
	 * Checks if the entity is valid for this crop block
	 * @param entityID  ID to check
	 * @return  True if the ID is valid, false otherwise
	 */
	private static boolean entityValid(String entityID) {
		return Config.animalCrops.get().contains(entityID) || Config.animalLilies.get().contains(entityID);
	}
}
