package knightminer.animalcrops.blocks;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.items.ItemAnimalSeeds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

public class BlockAnimalLily extends BlockAnimalCrops {

  protected static final VoxelShape LILY_PAD_AABB = makeCuboidShape(1, 0, 1, 15, 1, 15);

  public BlockAnimalLily(Properties props) {
    super(props);
  }

  /* Block properties */

  @Override
  protected ItemAnimalSeeds getSeed() {
    return Registration.lilySeeds;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return LILY_PAD_AABB;
  }

  /* Water logic */

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    IFluidState fluid = state.getFluidState();
    return fluid.getFluid().isIn(FluidTags.WATER) && fluid.isSource();
  }

  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return PlantType.Water;
  }

  @Override
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    super.onEntityCollision(state, world, pos, entity);
    if (entity instanceof BoatEntity) {
      world.destroyBlock(new BlockPos(pos), true);
    }
  }
}
