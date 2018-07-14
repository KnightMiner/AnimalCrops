package knightminer.animalcrops.world;

import java.util.Random;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenAnimalCrops implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		if(world.provider.getDimensionType() != DimensionType.OVERWORLD) {
			return;
		}

		// random chance
		if(random.nextInt(Config.animalBushChance) != 0) {
			return;
		}

		// find the surface
		int x = chunkX * 16 + 8 + random.nextInt(16);
		int z = chunkZ * 16 + 8 + random.nextInt(16);
		BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(x, 64, z));

		// insure we can place here
		if(!(world.getBlockState(top.down()).getBlock() instanceof BlockGrass) || !world.getBlockState(top).getBlock().isReplaceable(world, top)) {
			return;
		}

		world.setBlockState(top, AnimalCrops.bush.getDefaultState());
	}

}
