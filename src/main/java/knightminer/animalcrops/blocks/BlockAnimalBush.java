package knightminer.animalcrops.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BlockAnimalBush extends BushBlock implements IShearable {

	protected static final VoxelShape SHAPE = makeCuboidShape(2, 0, 2, 14, 16, 14);

	public BlockAnimalBush() {
		super(Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.CROP));
	}

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.getBlock() == Blocks.GRASS_BLOCK;
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.Plains;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

    /* Drops */
//
//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, BlockAccess world, BlockPos pos, IBlockState state, int fortune) {
//    	// just find a random seed from either list
//    	int crops = Config.animals.size();
//		int count = crops + Config.seaAnimals.size();
//		if(count > 0) {
//			int index = RANDOM.nextInt(count);
//			// if the number chosen is bigger than crops, its part of the sea animals
//			if (index >= crops) {
//				drops.add(Registration.lilySeeds.makeSeed(Config.seaAnimals.get(index - crops)));
//			} else {
//				drops.add(Registration.seeds.makeSeed(Config.animals.get(index)));
//			}
//		}
//    }

	@Override
	public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(@Nonnull net.minecraft.item.ItemStack item, IWorld world, BlockPos pos, int fortune) {
		// drop normal bush if sheared
		return Arrays.asList(new ItemStack(this));
	}
}
