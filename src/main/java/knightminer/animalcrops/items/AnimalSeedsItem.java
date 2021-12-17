package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item that allows planting the crop
 */
public class AnimalSeedsItem extends BlockItem {

	public AnimalSeedsItem(Block crops, Properties props) {
		super(crops, props);
	}

	// restore default, we call seeds seeds and crops crops
	@Override
	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	@Override
	public Component getName(ItemStack stack) {
    return Utils.getEntityID(stack.getTag())
                .flatMap(EntityType::byString)
                .map(EntityType::getDescriptionId)
                .map((key) -> new TranslatableComponent(this.getDescriptionId(), new TranslatableComponent(key)))
                .orElseGet(() -> new TranslatableComponent(this.getDescriptionId() + ".default"));
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, level, tooltip, flagIn);
    tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".tooltip"));
  }
}
