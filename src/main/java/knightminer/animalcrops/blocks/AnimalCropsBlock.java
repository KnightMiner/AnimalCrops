package knightminer.animalcrops.blocks;

import knightminer.animalcrops.blocks.entity.AnimalCropsBlockEntity;
import knightminer.animalcrops.core.AnimalTags;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.items.AnimalSeedsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * Base crop logic, used for plains crops directly
 */
public class AnimalCropsBlock extends CropBlock implements EntityBlock {
	private final TagKey<EntityType<?>> tag;
	public AnimalCropsBlock(Properties props, TagKey<EntityType<?>> tag) {
		super(props);
		this.tag = tag;
	}

	/* Crop properties */

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.PLAINS;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.is(AnimalTags.CROP_SOIL);
	}


	/**
	 * Gets the seed item for this crop
	 * @return  Item for this crop
	 */
	protected AnimalSeedsItem getSeed() {
		return Registration.seeds;
	}


	/* Seed logic */

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnimalCropsBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		// set the crop's entity
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof AnimalCropsBlockEntity animal) {
			Utils.getEntityID(stack.getTag()).ifPresent(animal::setEntity);
		}
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		// if the block changed, spawn the animal
		if (state.getBlock() != newState.getBlock()) {
			// assuming we have the tile entity to use
			if (getAge(state) >= getMaxAge()) {
				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof AnimalCropsBlockEntity animal) {
					animal.spawnAnimal();
				}
			}
			super.onRemove(state, level, pos, newState, isMoving);
			// otherwise, if the age lowered from max, spawn the animal
			// for right click harvest
		} else if (state.getBlock() == this && getAge(state) >= getMaxAge() && getAge(newState) < getMaxAge()) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof AnimalCropsBlockEntity animal) {
				animal.spawnAnimal();
			}
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		BlockEntity te = level.getBlockEntity(pos);
		ItemStack stack = new ItemStack(getSeed());
		if(te != null) {
			Utils.getEntityID(te.getTileData()).ifPresent(id->Utils.setEntityId(stack, id));
		}
		return stack;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		ITag<EntityType<?>> entityTag = ForgeRegistries.ENTITIES.tags().getTag(tag);
		if (entityTag.isBound() && !entityTag.isEmpty()) {
			for (EntityType<?> type : entityTag.stream().toList()) {
				items.add(Utils.setEntityId(new ItemStack(this), Objects.requireNonNull(type.getRegistryName()).toString()));
			}
		}
	}


	/* Bonemeal */

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return Config.canBonemeal.get();
	}

	@Override
	protected int getBonemealAgeIncrease(Level level) {
		return Mth.nextInt(level.random, 1, 3);
	}
}
