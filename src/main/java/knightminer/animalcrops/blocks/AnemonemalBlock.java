package knightminer.animalcrops.blocks;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.items.AnimalSeedsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Common logic for water and lava crops
 */
public class AnemonemalBlock extends AnimalCropsBlock {
  private final Supplier<? extends FlowingFluid> fluid;
  private final Tag<Fluid> tag;
  public AnemonemalBlock(Properties props, Supplier<List<? extends String>> animals, Supplier<FlowingFluid> fluid, Tag<Fluid> tag) {
    super(props, animals);
    this.fluid = fluid;
    this.tag = tag;
  }

  @Override
  protected AnimalSeedsItem getSeed() {
    return Registration.anemonemalSeeds;
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
    return state.isFaceSturdy(worldIn, pos, Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
    return fluid.is(tag) && fluid.getAmount() == 8 ? super.getStateForPlacement(context) : null;
  }


  /* Fluid logic */

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updateShape(stateIn, facing, facingState, world, currentPos, facingPos);
    if (!state.isAir()) {
      Fluid fluid = this.fluid.get();
      world.scheduleTick(currentPos, fluid, fluid.getTickDelay(world));
    }

    return state;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return fluid.get().getSource(false);
  }
}
