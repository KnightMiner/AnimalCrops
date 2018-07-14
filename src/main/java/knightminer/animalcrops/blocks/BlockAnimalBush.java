package knightminer.animalcrops.blocks;

import java.util.Arrays;
import java.util.List;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;

public class BlockAnimalBush extends BlockBush implements IShearable {

    protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.01, 0, 0.01, 0.9, 1, 0.9);
	public BlockAnimalBush() {
		super(Material.VINE);
    	this.setHardness(0.0F);
    	this.setSoundType(SoundType.PLANT);
	}

	/* Block properties */

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    	return EnumPlantType.Plains;
    }

    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDS;
    }


    /* Drops */

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    	// just find a random seed
		int count = Config.animals.size();
		if(count > 0) {
			ResourceLocation animal = Config.animals.get(RANDOM.nextInt(count));
			drops.add(AnimalCrops.seeds.makeSeed(animal));
		}
    }

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		// drop normal bush if sheared
		return Arrays.asList(new ItemStack(this));
	}
}
