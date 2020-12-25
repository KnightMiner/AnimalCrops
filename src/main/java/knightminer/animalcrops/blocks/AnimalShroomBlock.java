package knightminer.animalcrops.blocks;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.items.AnimalSeedsItem;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Logic for nether crops
 */
public class AnimalShroomBlock extends AnimalCropsBlock {
	public AnimalShroomBlock(Properties props, Supplier<List<? extends String>> animals) {
		super(props, animals);
	}

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.isIn(AnimalCrops.SHROOM_SOIL);
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.NETHER;
	}

	// override to remove light check
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos down = pos.down();
		if (state.getBlock() == this) {
			return worldIn.getBlockState(down).canSustainPlant(worldIn, down, Direction.UP, this);
		}
		return this.isValidGround(worldIn.getBlockState(down), worldIn, down);
	}

	@Override
	protected AnimalSeedsItem getSeed() {
		// TODO
		return Registration.seeds;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		int i = this.getAge(state);
		if (i < this.getMaxAge() && ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt(10) == 0)) {
			state = state.with(AGE, i + 1);
			worldIn.setBlockState(pos, state, 2);
			ForgeHooks.onCropsGrowPost(worldIn, pos, state);
		}
	}
}
