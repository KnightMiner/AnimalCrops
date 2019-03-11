package knightminer.animalcrops.tileentity;

import javax.annotation.Nonnull;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TileAnimalCrops extends TileEntity {
	public static final String ENTITY_DATA_TAG = "entity_data";

	private EntityLiving entity;
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	private boolean entityValid(ResourceLocation entityID) {
		if (this.getBlockType() == AnimalCrops.lily) {
			return Config.seaAnimals.contains(entityID);
		}
		return Config.animals.contains(entityID);
	}

	/**
	 * Gets the entity stored in this crop, reading from NBT if needed
	 * @return  The stored entity
	 */
	public EntityLiving getEntity(boolean updateNBT) {
		// if we have an entity, return that
		if(entity != null) {
			return entity;
		}

		// if an entity is set, we can create an entity
		NBTTagCompound data = this.getTileData();
		if(data.hasKey(Utils.ENTITY_TAG, 8)) {
			// entity must be whitelisted
			ResourceLocation entityID = new ResourceLocation(data.getString(Utils.ENTITY_TAG));
			if(!entityValid(entityID)) {
				if(updateNBT) {
					clearEntity(true);
				}
				return null;
			}

			// entity must be entity ageable
			Entity entityFromName = EntityList.createEntityByIDFromName(entityID, world);
			if(!(entityFromName instanceof EntityLiving)) {
				entityFromName.setDead();
				clearEntity(true);
				return null;
			}

			// we have the proper type
	    	entity = (EntityLiving)entityFromName;

			// if we have NBT already, use that
			if(data.hasKey(ENTITY_DATA_TAG, 10)) {
				NBTTagCompound entityData = data.getCompoundTag(ENTITY_DATA_TAG);
				try {
					entity.readFromNBT(entityData);
					// set for the client, since prev is needed for rotation but not stored to NBT
			        entity.prevRenderYawOffset = entity.prevRotationYawHead = entity.prevRotationYaw = entity.rotationYaw;
					return entity;
				} catch(Exception ex) {
					AnimalCrops.log.error("Exception caught loading entity from NBT", ex);
					if(updateNBT) {
						data.removeTag(ENTITY_DATA_TAG);
					}
				}
			}

			// if we do not have NBT or it was bad, set entity data
			if(entity instanceof EntityAgeable) {
				((EntityAgeable)entity).setGrowingAge(-24000);
			}
	        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);

	        // slime sizes should not be bigger than 2
	        if(entity instanceof EntitySlime) {
	        	EntitySlime slime = (EntitySlime)entity;
	        	if(slime.getSlimeSize() > 2) {
	        		Utils.setSlimeSize(slime, 2);
	        	}
	        }

	        entity.rotationYaw = MathHelper.wrapDegrees(world.rand.nextInt(4) * 90.0F); // face randomly in one of 4 directions
	        entity.rotationYawHead = entity.rotationYaw;
	        entity.renderYawOffset = entity.rotationYaw;
	        if(updateNBT) {
	        	data.setTag(ENTITY_DATA_TAG, entity.writeToNBT(new NBTTagCompound()));
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
		NBTTagCompound data = this.getTileData();
		if(clearType) {
			data.removeTag(Utils.ENTITY_TAG);
		}
		data.removeTag(ENTITY_DATA_TAG);
		this.markDirty();
	}

	/**
	 * Sets the entity into the TE
	 * @param entityID  Entity ID to set
	 */
	public void setAnimal(ResourceLocation entityID) {
		if(world.isRemote || entityID == null) {
			return;
		}
		this.getTileData().setString(Utils.ENTITY_TAG, entityID.toString());
		// if we have fancy rendering, create the entity now so the client can grab it
		if(Config.fancyCropRendering) {
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
        world.spawnEntity(entity);
        entity.playLivingSound();
    }

	/**
	 * Spawns the entity then resets the crop's NBT
	 */
	public void spawnAndReset() {
		spawnAnimal();
		clearEntity(false);
		if(Config.fancyCropRendering) {
			getEntity(true);
			markDirty();
		}
	}

	public void setDead() {
		if(entity != null) {
			entity.setDead();
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
	public NBTTagCompound getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		// note that this sends all of the tile data. you should change this if you use additional tile data
		NBTTagCompound tag = writeToNBT(new NBTTagCompound());
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		readFromNBT(tag);
	}
}
