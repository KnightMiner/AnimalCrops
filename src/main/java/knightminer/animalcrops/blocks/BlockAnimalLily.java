package knightminer.animalcrops.blocks;

import java.util.List;

import javax.annotation.Nullable;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.items.ItemAnimalSeeds;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockAnimalLily extends BlockAnimalCrops {

    @Override
	protected ItemAnimalSeeds getSeed() {
        return AnimalCrops.lilySeeds;
    }

    /* Water logic */

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Water;
    }

    @Override
	public boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0;
    }

    /* New Bounds */

    protected static final AxisAlignedBB LILY_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.09375D, 0.9375D);

    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return LILY_AABB;
    }

    /* Boats break block like lily pads */

    @Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        if (!(entityIn instanceof EntityBoat)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, LILY_AABB);
        }
    }

    @Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);

        if (entity instanceof EntityBoat) {
            world.destroyBlock(new BlockPos(pos), true);
        }
    }
}
