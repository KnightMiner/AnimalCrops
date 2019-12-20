package knightminer.animalcrops.blocks;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.items.ItemAnimalSeeds;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockAnimalCrops extends CropsBlock {

	public BlockAnimalCrops(Properties props) {
		super(props);
	}

	/* Crop properties */

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.getBlock() == Blocks.GRASS_BLOCK;
	}

	/**
	 * Gets the seed item for this crop
	 * @return  Item for this crop
	 */
	protected ItemAnimalSeeds getSeed() {
		return Registration.seeds;
	}


	/* Seed logic */

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileAnimalCrops();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		// set the crop's entity
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileAnimalCrops) {
			Utils.getEntityID(stack.getTag()).ifPresent((id) -> ((TileAnimalCrops)te).setEntity(id));
		}
	}

	@Deprecated
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// if the block changed, spawn the animal
		if(state.getBlock() != newState.getBlock()) {
			// assuming we have the tile entity to use
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileAnimalCrops) {
				if(getAge(state) >= getMaxAge()) {
					((TileAnimalCrops)te).spawnAnimal();
				}
			}

			super.onReplaced(state, world, pos, newState, isMoving);
			// otherwise, if the age lowered from max, spawn the animal
			// for right click harvest
		} else if(state.getBlock() == this && getAge(state) >= getMaxAge() && getAge(newState) < getMaxAge()) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileAnimalCrops) {
				((TileAnimalCrops)te).spawnAnimal();
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
