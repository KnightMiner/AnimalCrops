package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class ItemAnimalLily extends ItemAnimalSeeds {

	public ItemAnimalLily(Block crops) {
		super(crops);
	}

    @Override
    public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
    	return EnumPlantType.Water;
    }

    @Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for(ResourceLocation entity : Config.seaAnimals) {
                items.add(makeSeed(entity));
            }
        }
    }

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		// logic moved to onItemRightClick to get water support
		return EnumActionResult.PASS;
	}

    @Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

    	// need to include liquids in the raytrace
        RayTraceResult trace = this.rayTrace(world, player, true);
        ItemStack seeds = player.getHeldItem(hand);

        if (trace == null) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, seeds);
        }

        if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = trace.getBlockPos();

            if (!world.isBlockModifiable(player, pos) || !player.canPlayerEdit(pos.offset(trace.sideHit), trace.sideHit, seeds)) {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, seeds);
            }

            BlockPos up = pos.up();
            IBlockState state = world.getBlockState(pos);
            if (state.getMaterial() == Material.WATER && state.getValue(BlockLiquid.LEVEL) == 0 && world.isAirBlock(up)) {
            	IBlockState plant = this.getPlant(world, pos);
                if(!world.setBlockState(up, plant) || world.getBlockState(up).getBlock() != this.crops) {
                	return new ActionResult<ItemStack>(EnumActionResult.FAIL, seeds);
                }

                this.crops.onBlockPlacedBy(world, up, plant, player, seeds);

                if (player instanceof EntityPlayerMP) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, up, seeds);
                }

                if (!player.capabilities.isCreativeMode) {
                    seeds.shrink(1);
                }

                world.playSound(player, pos, SoundEvents.BLOCK_WATERLILY_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, seeds);
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, seeds);
    }
}
