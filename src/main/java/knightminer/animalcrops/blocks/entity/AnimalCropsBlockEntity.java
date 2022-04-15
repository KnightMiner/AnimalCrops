package knightminer.animalcrops.blocks.entity;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.AnimalTags;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnimalCropsBlockEntity extends BlockEntity {
	/** Tag to use to store the random direction */
	public static final String TAG_DIRECTION = "direction";

	/**
	 * Cached entity from {@link #getEntity(boolean)}.
	 * Typically null except on the client side (only cached during fancy crop rendering)
	 * */
	private LivingEntity entity;

	public AnimalCropsBlockEntity(BlockPos pos, BlockState state) {
		super(Registration.cropsTE, pos, state);
	}


	/* Getters and setters */

	/**
	 * Sets the entity into the TE
	 * @param entityID  Entity ID to set
	 */
	public void setEntity(String entityID) {
		// only set if valid
		if (!entityValid(entityID)) {
			return;
		}

		CompoundTag data = this.getTileData();
		data.putString(Utils.ENTITY_TAG, entityID);
		assert level != null;
		if (!level.isClientSide()) {
			data.putInt(TAG_DIRECTION, level.random.nextInt(4));
		}
		this.setChanged();
	}

	/**
	 * Gets the entity for this crop
	 * @param  cacheEntity  If true, pull the entity from the cache and cache the result. Typically only used clientside
	 * @return  The entity for this crop
	 */
	@SuppressWarnings("Convert2MethodRef")
	@Nullable
	public LivingEntity getEntity(boolean cacheEntity) {
		if (cacheEntity && entity != null) {
			return entity;
		}

		// create the entity from the tile data
		assert level != null;
		Entity created = Utils.getEntityID(this.getTileData())
													.filter(AnimalCropsBlockEntity::entityValid)
													.flatMap(loc -> EntityType.byString(loc))
													.map((type) -> type.create(level))
													.orElse(null);
		if (created == null) {
			return null;
		}

		// if the entity is not MobEntity, discard it
		// should not happen as all spawn eggs are MobEntity
		if (!(created instanceof LivingEntity entity)) {
			created.remove(RemovalReason.DISCARDED);
			AnimalCrops.log.error("Attempted to create invalid non-living entity " + created.getType());
			return null;
		}

		// set the age for ageable entities
		if (getBlockState().getFluidState().getType() == Fluids.WATER) {
			entity.wasTouchingWater = true;
		}
		float angle = this.getAngle();
		entity.setYRot(angle);
		entity.yRotO = angle;
		entity.yHeadRotO = entity.yHeadRot = angle;
		entity.yBodyRotO = entity.yBodyRot = angle;

		if (entity instanceof Mob mob) {
			mob.setBaby(true);
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
			return Direction.SOUTH.toYRot();
		}
		return Direction.from2DDataValue(index).toYRot();
	}

	/**
	 * Spawns the current entity into the world then clears it
	 */
	public void spawnAnimal() {
		// if we have no entity, give up
		LivingEntity entity = getEntity(false);
		if(entity == null) {
			return;
		}

		// set position
		entity.setPos(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);

		// set entity data where relevant
		assert level != null;
		Mob mob = null;
		if (level instanceof ServerLevelAccessor accessor && entity instanceof Mob) {
			mob = (Mob) entity;
			mob.finalizeSpawn(accessor, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWN_EGG, null, null);
		}

		// slime sizes should not be bigger than 2
		if(entity instanceof Slime slime && slime.getSize() > 2) {
			Utils.setSlimeSize(slime, 2);
		}

		// spawn
		entity.level = level;
		level.addFreshEntity(entity);
		if (mob != null) {
			mob.playAmbientSound();
		}
	}


	/* NBT */

	@Nonnull
	@Override
	public CompoundTag getUpdateTag() {
		// only the data in the forge tile data tag needs to be sent over the network
		return getTileData().copy();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		if (tag != null) {
			getTileData().merge(tag);
		}
	}

	/* Helpers */

	/**
	 * Checks if the entity is valid for this crop block
	 * @param entityID  ID to check
	 * @return  True if the ID is valid, false otherwise
	 */
	private static boolean entityValid(String entityID) {
		ResourceLocation loc = ResourceLocation.tryParse(entityID);
		if (loc != null && ForgeRegistries.ENTITIES.containsKey(loc)) {
			EntityType<?> type = ForgeRegistries.ENTITIES.getValue(loc);
			return type != null && type.is(AnimalTags.PLANTABLE);
		}
		return false;
	}
}
