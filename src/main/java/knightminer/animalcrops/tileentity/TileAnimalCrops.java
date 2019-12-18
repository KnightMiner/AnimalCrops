package knightminer.animalcrops.tileentity;

import knightminer.animalcrops.AnimalCrops;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileAnimalCrops extends TileEntity {
	public static final String ENTITY_DATA_TAG = "entity_data";

	private MobEntity entity;

	public TileAnimalCrops() {
		super(Registration.cropsTE);
	}

	/**
	 * Checks if the entity is valid for this crop block
	 * @param entityID  ID to check
	 * @return  True if the ID is valid, false otherwise
	 */
	private boolean entityValid(String entityID) {
		return Config.animalCrops.get().contains(entityID) || Config.animalLilies.get().contains(entityID);
	}

	/**
	 * Gets the entity stored in this crop, reading from NBT if needed
	 * @return  The stored entity
	 */
	public MobEntity getEntity(boolean updateNBT) {
		// TODO: this function is a pretty big mess, see if it can be cleaned up
		// if we have an entity, return that
		if(entity != null) {
			return entity;
		}

		// if an entity is set, we can create an entity
		CompoundNBT data = this.getTileData();
		String entityID = Utils.getEntityID(data).orElse(null);
		if(entityID != null) {
			// entity must be whitelisted
			if(!entityValid(entityID)) {
				if(updateNBT) {
					clearEntity(true);
				}
				return null;
			}

			// entity must be entity mob entity
			EntityType<?> type = EntityType.byKey(entityID.toString()).orElse(null);
			if (type == null) { // || !MobEntity.class.isAssignableFrom(type.getClass())) {
				clearEntity(true);
				return null;
			}

			Entity created = type.create(world);
			if (!(created instanceof MobEntity)) {
				created.remove();
				clearEntity(true);
				return null;
			}
			entity = (MobEntity)created;

			// if we have NBT already, use that
			if(data.contains(ENTITY_DATA_TAG, 10)) {
				CompoundNBT entityData = data.getCompound(ENTITY_DATA_TAG);
				try {
					entity.read(entityData);
					// set for the client, since prev is needed for rotation but not stored to NBT
					entity.prevRenderYawOffset = entity.prevRotationYawHead = entity.prevRotationYaw = entity.rotationYaw;
					return entity;
				} catch(Exception ex) {
					AnimalCrops.log.error("Exception caught loading entity from NBT", ex);
					if(updateNBT) {
						data.remove(ENTITY_DATA_TAG);
					}
				}
			}

			// if we do not have NBT or it was bad, set entity data
			if(entity instanceof AgeableEntity) {
				((AgeableEntity)entity).setGrowingAge(-24000);
			}
			entity.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entity)), SpawnReason.SPAWN_EGG, null, null);

			// slime sizes should not be bigger than 2
			if(entity instanceof SlimeEntity) {
				SlimeEntity slime = (SlimeEntity)entity;
				if(slime.getSlimeSize() > 2) {
					Utils.setSlimeSize(slime, 2);
				}
			}

			entity.rotationYaw = MathHelper.wrapDegrees(world.rand.nextInt(4) * 90.0F); // face randomly in one of 4 directions
			entity.rotationYawHead = entity.rotationYaw;
			entity.renderYawOffset = entity.rotationYaw;
			if(updateNBT) {
				data.put(ENTITY_DATA_TAG, entity.writeWithoutTypeId(new CompoundNBT()));
			}
			this.markDirty();
		}
		return entity;
	}

	/**
	 * Removes the current entity from the TE and its NBT
	 * @param clearType  if true, removes the entity type
	 */
	private void clearEntity(boolean clearType) {
		entity = null;
		CompoundNBT data = this.getTileData();
		if(clearType) {
			data.remove(Utils.ENTITY_TAG);
		}
		data.remove(ENTITY_DATA_TAG);
		this.markDirty();
	}

	/**
	 * Sets the entity into the TE
	 * @param entityID  Entity ID to set
	 */
	public void setAnimal(String entityID) {
		if(world.isRemote || entityID == null) {
			return;
		}

		// only set if valid
		if (!entityValid(entityID)) {
			return;
		}

		this.getTileData().putString(Utils.ENTITY_TAG, entityID);
		// if we have fancy rendering, create the entity now so the client can grab it
		// TODO: not good to do serverside?
		if(Config.fancyCropRendering.get()) {
			getEntity(true);
		}
		this.markDirty();
	}

	/**
	 * Spawns the current entity into the world then clears it
	 */
	public void spawnAnimal() {
		// if we have no entity, give up
		if(getEntity(false) == null) {
			return;
		}

		// set position
		entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

		// spawn
		entity.setWorld(world);
		world.addEntity(entity);
		entity.playAmbientSound();
	}

	/**
	 * Spawns the entity then resets the crop's NBT
	 */
	public void spawnAndReset() {
		spawnAnimal();
		clearEntity(false);
		// TODO: should not be serverside?
		if(Config.fancyCropRendering.get()) {
			getEntity(true);
			markDirty();
		}
	}

	public void setDead() {
		if(entity != null) {
			entity.remove();
		}
	}

	/* NBT */

    @Override
	public void setWorld(World world) {
        super.setWorld(world);
        if(entity != null) {
        	entity.setWorld(world);
        }
    }

	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		// note that this sends all of the tile data. you should change this if you use additional tile data
		CompoundNBT tag = write(new CompoundNBT());
		return new SUpdateTileEntityPacket(this.getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getNbtCompound();
		read(tag);
	}
}
