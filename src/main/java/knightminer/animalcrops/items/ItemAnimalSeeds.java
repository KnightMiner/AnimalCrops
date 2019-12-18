package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemAnimalSeeds extends BlockItem {

	public ItemAnimalSeeds(Block crops, Properties props) {
		super(crops, props);
	}

	// restore default, we call seeds seeds and crops crops
  @Override
  public String getTranslationKey() {
    return this.getDefaultTranslationKey();
  }

	@Override
  public ITextComponent getDisplayName(ItemStack stack) {
    return Utils.getEntityID(stack.getTag())
                .flatMap(EntityType::byKey)
                .map(EntityType::getTranslationKey)
                .map((key) -> new TranslationTextComponent(this.getTranslationKey(), new TranslationTextComponent(key)))
                .orElseGet(() -> new TranslationTextComponent(this.getTranslationKey() + ".default"));
  }

  @Deprecated
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
   * @deprecated  Use {@link Utils::setEntityId(ItemStack, String)}
   */
  @Deprecated
  public ItemStack makeSeed(String entity) {
    return Utils.setEntityId(new ItemStack(this), entity);
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      for(String entity : Config.animalCrops.get()) {
        items.add(Utils.setEntityId(new ItemStack(this), entity));
      }
    }
  }
}
