package knightminer.animalcrops.tileentity;

import javax.annotation.Nonnull;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
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

	private EntityCreature entity;
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	/**
	 * Gets the entity stored in this crop, reading from NBT if needed
	 * @return  The stored entity
	 */
	public EntityCreature getEntity() {
		// if we have an entity, return that
		if(entity != null) {
			return entity;
		}

		// if an entity is set, we can create an entity
		if(this.getTileData().hasKey(Utils.ENTITY_TAG, 8)) {
			// entity must be whitelisted
			ResourceLocation entityID = new ResourceLocation(this.getTileData().getString(Utils.ENTITY_TAG));
			if(!Config.animals.contains(entityID)) {
				this.getTileData().removeTag(Utils.ENTITY_TAG);
				this.getTileData().removeTag(ENTITY_DATA_TAG);
				this.markDirty();
				return null;
			}

			// entity must be entity ageable
			Entity entityFromName = EntityList.createEntityByIDFromName(entityID, world);
			if(!(entityFromName instanceof EntityCreature)) {
				entityFromName.setDead();
				this.getTileData().removeTag(Utils.ENTITY_TAG);
				this.getTileData().removeTag(ENTITY_DATA_TAG);
				this.markDirty();
				return null;
			}

			// we have the proper type
	    	entity = (EntityCreature)entityFromName;

			// if we have NBT already, use that
			if(this.getTileData().hasKey(ENTITY_DATA_TAG, 10)) {
				NBTTagCompound entityData = this.getTileData().getCompoundTag(ENTITY_DATA_TAG);
				try {
					entity.readFromNBT(entityData);
					// set for the client, since prev is needed for rotation but not stored to NBT
			        entity.prevRenderYawOffset = entity.prevRotationYawHead = entity.prevRotationYaw = entity.rotationYaw;
					return entity;
				} catch(Exception ex) {
					AnimalCrops.log.error("Exception caught loading entity from NBT", ex);
					this.getTileData().removeTag(ENTITY_DATA_TAG);
				}
			}

			// if we do not have NBT or it was bad, set entity data
			if(entity instanceof EntityAgeable) {
				((EntityAgeable)entity).setGrowingAge(-24000);
			}
	        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
	        entity.rotationYaw = MathHelper.wrapDegrees(world.rand.nextInt(4) * 90.0F); // face randomly in one of 4 directions
	        entity.rotationYawHead = entity.rotationYaw;
	        entity.renderYawOffset = entity.rotationYaw;
			this.getTileData().setTag(ENTITY_DATA_TAG, entity.writeToNBT(new NBTTagCompound()));
			this.markDirty();
		}
	    return entity;
	}

	public void setAnimal(ResourceLocation entityID) {
		if(world.isRemote || entityID == null) {
			return;
		}
		this.getTileData().setString(Utils.ENTITY_TAG, entityID.toString());
		// if we have fancy rendering, create the entity now so the client can grab it
		if(Config.fancyCropRendering) {
			getEntity();
		}
		this.markDirty();
	}

	public void spawnAnimal() {
		// if we have no entity, give up
		if(getEntity() == null) {
			return;
		}

        // set position
        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        // spawn
		entity.setWorld(world);
        world.spawnEntity(entity);
        entity.playLivingSound();
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
		NBTTagCompound tag = getTileData().copy();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		readFromNBT(tag);
	}
}
