package knightminer.animalcrops.blocks;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.items.AnimalSeedsItem;
import knightminer.animalcrops.tileentity.AnimalCropsTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Base crop logic, used for plains crops directly
 */
public class AnimalCropsBlock extends CropsBlock {
	protected final Supplier<List<? extends String>> animals;
	public AnimalCropsBlock(Properties props, Supplier<List<? extends String>> animals) {
		super(props);
		this.animals = animals;
	}

	/* Crop properties */

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.PLAINS;
	}

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.isIn(AnimalCrops.CROP_SOIL);
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
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AnimalCropsTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		// set the crop's entity
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof AnimalCropsTileEntity) {
			Utils.getEntityID(stack.getTag()).ifPresent(((AnimalCropsTileEntity)te)::setEntity);
		}
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// if the block changed, spawn the animal
		if(state.getBlock() != newState.getBlock()) {
			// assuming we have the tile entity to use
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof AnimalCropsTileEntity) {
				if(getAge(state) >= getMaxAge()) {
					((AnimalCropsTileEntity)te).spawnAnimal();
				}
			}

			super.onReplaced(state, world, pos, newState, isMoving);
			// otherwise, if the age lowered from max, spawn the animal
			// for right click harvest
		} else if(state.getBlock() == this && getAge(state) >= getMaxAge() && getAge(newState) < getMaxAge()) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof AnimalCropsTileEntity) {
				((AnimalCropsTileEntity)te).spawnAnimal();
			}
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		TileEntity te = world.getTileEntity(pos);
		ItemStack stack = new ItemStack(getSeed());
		if(te != null) {
			Utils.getEntityID(te.getTileData()).ifPresent((id)->Utils.setEntityId(stack, id));
		}
		return stack;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for (String id : animals.get()) {
			items.add(Utils.setEntityId(new ItemStack(this), id));
		}
	}


	/* Bonemeal */

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return Config.canBonemeal.get();
	}

	@Override
	protected int getBonemealAgeIncrease(World world) {
		return MathHelper.nextInt(world.rand, 1, 3);
	}
}
