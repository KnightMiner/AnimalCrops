package knightminer.animalcrops.blocks;

import knightminer.animalcrops.core.AnimalTags;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.items.AnimalSeedsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.Random;

/**
 * Logic for nether crops
 */
public class AnimalShroomBlock extends AnimalCropsBlock {
	public AnimalShroomBlock(Properties props, TagKey<EntityType<?>> tag) {
		super(props, tag);
	}


	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.is(AnimalTags.SHROOM_SOIL);
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.NETHER;
	}

	// override to remove light check
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos down = pos.below();
		BlockState below = level.getBlockState(down);
		if (state.getBlock() == this) {
			return below.canSustainPlant(level, down, Direction.UP, this);
		}
		return this.mayPlaceOn(below, level, down);
	}

	@Override
	protected AnimalSeedsItem getSeed() {
		return Registration.seeds;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		int i = this.getAge(state);
		if (i < this.getMaxAge() && ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt(10) == 0)) {
			state = state.setValue(AGE, i + 1);
			level.setBlock(pos, state, 2);
			ForgeHooks.onCropsGrowPost(level, pos, state);
		}
	}
}
