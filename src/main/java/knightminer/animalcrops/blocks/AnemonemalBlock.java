package knightminer.animalcrops.blocks;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.items.AnimalSeedsItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Common logic for water and lava crops
 */
public class AnemonemalBlock extends AnimalCropsBlock implements ILiquidContainer {
  private final Supplier<? extends FlowingFluid> fluid;
  private final ITag<Fluid> tag;
  public AnemonemalBlock(Properties props, Supplier<List<? extends String>> animals, Supplier<FlowingFluid> fluid, ITag<Fluid> tag) {
    super(props, animals);
    this.fluid = fluid;
    this.tag = tag;
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
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    return fluid.isTagged(tag) && fluid.getLevel() == 8 ? super.getStateForPlacement(context) : null;
  }

  /* Fluid logic */

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
    if (!state.isAir(world, currentPos)) {
      Fluid fluid = this.fluid.get();
      world.getPendingFluidTicks().scheduleTick(currentPos, fluid, fluid.getTickRate(world));
    }

    return state;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return fluid.get().getStillFluidState(false);
  }

  @Override
  public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
    return false;
  }

  @Override
  public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
    return false;
  }
}
