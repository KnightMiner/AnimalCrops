package knightminer.animalcrops.json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

/**
 * Function to copy the animal from TE data to the seed item
 */
public class SetAnimalLootFunction extends LootItemConditionalFunction {
  public static final Serializer SERIALIZER = new Serializer();

  protected SetAnimalLootFunction(LootItemCondition[] conditions) {
    super(conditions);
  }

  @Override
  public LootItemFunctionType getType() {
    return Registration.Loot.setAnimalFunction;
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
    if(te != null) {
      Utils.getEntityID(te.getTileData()).ifPresent(id->Utils.setEntityId(stack, id));
    }
    return stack;
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
  }

  private static class Serializer extends LootItemConditionalFunction.Serializer<SetAnimalLootFunction> {
    @Override
    public SetAnimalLootFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
      return new SetAnimalLootFunction(conditions);
    }
  }
}
