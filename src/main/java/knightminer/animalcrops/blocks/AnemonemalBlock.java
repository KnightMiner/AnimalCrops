package knightminer.animalcrops.blocks;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.items.AnimalSeedsItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class AnemonemalBlock extends AnimalCropsBlock implements ILiquidContainer {
  public AnemonemalBlock(Properties props, Supplier<List<? extends String>> animals) {
    super(props, animals);
  }

  @Override
  protected AnimalSeedsItem getSeed() {
    return Registration.anemonemalSeeds;
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader world, BlockPos pos) {
    return state.isSolidSide(world, pos, Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    IFluidState fluid = context.getWorld().getFluidState(context.getPos());
    return fluid.isTagged(FluidTags.WATER) && fluid.getLevel() == 8 ? super.getStateForPlacement(context) : null;
  }

  /* Water logic */

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
    if (!state.isAir(world, currentPos)) {
      world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }

    return state;
  }

  @Deprecated
  @Override
  public IFluidState getFluidState(BlockState state) {
    return Fluids.WATER.getStillFluidState(false);
  }

  @Override
  public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
    return false;
  }

  @Override
  public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, IFluidState fluidState) {
    return false;
  }
}
