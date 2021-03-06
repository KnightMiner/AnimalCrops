package knightminer.animalcrops.json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Config.AnimalCropType;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RandomAnimalLootFunction extends LootFunction {
  private static final Map<String,AnimalCropType> TYPES = new HashMap<>();

  private final String type;
  private final AnimalCropType animalType;
  protected RandomAnimalLootFunction(ILootCondition[] conditions, String type, AnimalCropType animalType) {
    super(conditions);
    this.type = type;
    this.animalType = animalType;
  }

  @Override
  protected ItemStack doApply(ItemStack stack, LootContext context) {
    List<? extends String> list = animalType.getRandomDrops();
    // prevent crash if empty, the config condition should handle this though
    if (list.isEmpty()) {
      AnimalCrops.log.error("Received empty animal list for {}, a condition is missing in the loot table", type);
    } else {
      String id = list.get(context.getRandom().nextInt(list.size()));
      Utils.setEntityId(stack, id);
    }
    return stack;
  }

  @Nonnull
  @Override
  public Set<LootParameter<?>> getRequiredParameters() {
    return ImmutableSet.of();
  }

  @Override
  public LootFunctionType getFunctionType() {
    return Registration.Loot.randomAnimalFunction;
  }

  public static class Serializer extends LootFunction.Serializer<RandomAnimalLootFunction> {
    @Override
    public void serialize(JsonObject json, RandomAnimalLootFunction randAnimal, JsonSerializationContext context) {
      super.serialize(json, randAnimal, context);
      json.addProperty("prop", randAnimal.type);
    }

    @Nonnull
    @Override
    public RandomAnimalLootFunction deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext ctx, @Nonnull ILootCondition[] conditions) {
      String type = JSONUtils.getString(json, "type").toLowerCase(Locale.ROOT);
      AnimalCropType animalCropType = TYPES.get(type);
      if (animalCropType == null) {
        throw new JsonSyntaxException("Invalid animal type '" + type + "'");
      }
      return new RandomAnimalLootFunction(conditions, type, animalCropType);
    }
  }

  /* Setup prop list */
  static {
    TYPES.put("crops", Config.animalCrops);
    TYPES.put("anemonemal", Config.anemonemals);
    TYPES.put("shrooms", Config.animalShrooms);
    TYPES.put("magnemone", Config.magnemones);
  }
}
