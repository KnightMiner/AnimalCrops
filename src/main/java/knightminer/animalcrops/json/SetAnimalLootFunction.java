package knightminer.animalcrops.json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import knightminer.animalcrops.core.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;
import java.util.Set;

public class SetAnimalLootFunction extends LootFunction {
  protected SetAnimalLootFunction(ILootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  protected ItemStack doApply(ItemStack stack, LootContext context) {
    TileEntity te = context.get(LootParameters.BLOCK_ENTITY);
    if(te != null) {
      Utils.getEntityID(te.getTileData()).ifPresent((id)->Utils.setEntityId(stack, id));
    }
    return stack;
  }

  @Nonnull
  @Override
  public Set<LootParameter<?>> getRequiredParameters() {
    return ImmutableSet.of(LootParameters.BLOCK_ENTITY);
  }

  public static class Serializer extends LootFunction.Serializer<SetAnimalLootFunction> {
    public Serializer(ResourceLocation location) {
      super(location, SetAnimalLootFunction.class);
    }

    @Nonnull
    @Override
    public SetAnimalLootFunction deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext ctx, @Nonnull ILootCondition[] conditions) {
      return new SetAnimalLootFunction(conditions);
    }
  }
}
