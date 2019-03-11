package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

@SuppressWarnings("deprecation")
public class ItemAnimalSeeds extends ItemSeeds {

	protected Block crops;
	public ItemAnimalSeeds(Block crops) {
		super(crops, Blocks.GRASS);
		this.crops = crops;
	}

    @Override
    public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
    	return EnumPlantType.Plains;
    }

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
        String entityKey = EntityList.getTranslationName(Utils.getEntityID(stack.getTagCompound()));
        if (entityKey != null) {
            return I18n.translateToLocalFormatted(this.getUnlocalizedName() + ".name", I18n.translateToLocal("entity." + entityKey + ".name"));
        }

        return I18n.translateToLocal(this.getUnlocalizedName() + ".default.name");
    }

    public ItemStack makeSeed(ResourceLocation entity) {
    	if(entity == null) {
        	return new ItemStack(this);
    	}
		return makeSeed(entity.toString());
    }

    /**
     * Makes a seed stack from the given entity ID
     * @param entity  Entity ID
     * @return  Seed containing that entity
     */
    public ItemStack makeSeed(String entity) {
    	if(entity == null) {
        	return new ItemStack(this);
    	}
    	ItemStack stack = new ItemStack(this);
    	NBTTagCompound tags = new NBTTagCompound();
    	tags.setString(Utils.ENTITY_TAG, entity);
    	stack.setTagCompound(tags);
    	return stack;
    }

    @Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for(ResourceLocation entity : Config.animals) {
                items.add(makeSeed(entity));
            }
        }
    }

    @Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        BlockPos up = pos.up();
        if (side == EnumFacing.UP && player.canPlayerEdit(pos.offset(side), side, stack) && state.getBlock() instanceof BlockGrass && world.isAirBlock(up)) {
        	IBlockState plant = this.getPlant(world, pos);
            if(!world.setBlockState(up, plant) || world.getBlockState(up).getBlock() != this.crops) {
            	return EnumActionResult.FAIL;
            }

            this.crops.onBlockPlacedBy(world, up, plant, player, stack);

            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos.up(), stack);
            }

            stack.shrink(1);
            world.playSound(player, pos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }
}
