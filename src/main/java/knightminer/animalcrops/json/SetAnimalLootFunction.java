package knightminer.animalcrops.json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;

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

  @Override
  public LootFunctionType getFunctionType() {
    return Registration.Loot.setAnimalFunction;
  }

  public static class Serializer extends LootFunction.Serializer<SetAnimalLootFunction> {
    @Override
    public SetAnimalLootFunction deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext ctx, @Nonnull ILootCondition[] conditions) {
      return new SetAnimalLootFunction(conditions);
    }
  }
}
