package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AnimalSeedsItem extends BlockItem {

	public AnimalSeedsItem(Block crops, Properties props) {
		super(crops, props);
	}

	// restore default, we call seeds seeds and crops crops
  @Override
  public String getTranslationKey() {
    return this.getDefaultTranslationKey();
  }

	@SuppressWarnings("Convert2MethodRef")
	@Override
  public ITextComponent getDisplayName(ItemStack stack) {
    return Utils.getEntityID(stack.getTag())
                .flatMap(loc -> EntityType.byKey(loc))
                .map(EntityType::getTranslationKey)
                .map((key) -> new TranslationTextComponent(this.getTranslationKey(), new TranslationTextComponent(key)))
                .orElseGet(() -> new TranslationTextComponent(this.getTranslationKey() + ".default"));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }
}
