package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class AnimalLilyItem extends AnimalSeedsItem {

	public AnimalLilyItem(Block crops, Properties props) {
		super(crops, props);
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
    if (this.isInGroup(tab)) {
      for(String entity : Config.animalLilies.get()) {
        items.add(Utils.setEntityId(new ItemStack(this), entity));
      }
    }
  }

  /* Special placement logic */
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return ActionResultType.PASS;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    RayTraceResult trace = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
    if (trace.getType() == RayTraceResult.Type.MISS) {
      return new ActionResult<>(ActionResultType.PASS, stack);
    }

    // must be block type
    if (trace.getType() != RayTraceResult.Type.BLOCK) {
      return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    // make sure it is modifiable
    BlockRayTraceResult blockTrace = (BlockRayTraceResult)trace;
    BlockPos pos = blockTrace.getPos();
    Direction direction = blockTrace.getFace();
    if (!world.isBlockModifiable(player, pos) || !player.canPlayerEdit(pos.offset(direction), direction, stack)) {
      return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    // make sure the area above is valid
    BlockPos above = pos.up();
    if (!world.isAirBlock(above) || (world.getBlockState(pos).getMaterial() != Material.ICE && world.getFluidState(pos).getFluid() != Fluids.WATER)) {
      return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    // forge snapshots
    BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(world, above);
    world.setBlockState(above, getBlock().getDefaultState(), 11);
    if (ForgeEventFactory.onBlockPlace(player, snapshot, Direction.UP)) {
      snapshot.restore(true, false);
      return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    BlockState placed = world.getBlockState(above);
    if (placed.getBlock() == getBlock()) {
      onBlockPlaced(above, world, player, stack, placed);
      getBlock().onBlockPlacedBy(world, above, placed, player, stack);
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, above, stack);
      }
    }

    // post placement stuff
    if (!player.isCreative()) {
      stack.shrink(1);
    }
    player.addStat(Stats.ITEM_USED.get(this));
    world.playSound(player, pos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    return new ActionResult<>(ActionResultType.SUCCESS, stack);
  }
}
