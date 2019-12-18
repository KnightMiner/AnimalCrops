package knightminer.animalcrops.world;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import net.minecraft.block.GrassBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

// TODO: convert to feature
public class WorldGenAnimalCrops implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider) {
		if(world.dimension.getType() != DimensionType.OVERWORLD) {
			return;
		}

		// random chance
		if(random.nextInt(Config.animalBushChance.get()) != 0) {
			return;
		}

		// find the surface
		int x = chunkX * 16 + 8 + random.nextInt(16);
		int z = chunkZ * 16 + 8 + random.nextInt(16);
		BlockPos top = null;//world.getTopSolidOrLiquidBlock(new BlockPos(x, 64, z));

		// insure we can place here
		if(!(world.getBlockState(top.down()).getBlock() instanceof GrassBlock) || !world.getBlockState(top).isReplaceable(null)) {
			return;
		}

		world.setBlockState(top, Registration.bush.getDefaultState());
	}

}
