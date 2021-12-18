package knightminer.animalcrops.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Config.AnimalCropType;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Sets an item's entity to random
 */
public class RandomAnimalLootFunction extends LootItemConditionalFunction {
  private static final Map<String,AnimalCropType> TYPES = new HashMap<>();
  public static final Serializer SERIALIZER = new Serializer();

  private final String type;
  private final AnimalCropType animalType;
  protected RandomAnimalLootFunction(LootItemCondition[] conditions, String type, AnimalCropType animalType) {
    super(conditions);
    this.type = type;
    this.animalType = animalType;
  }

  @Override
  public LootItemFunctionType getType() {
    return Registration.Loot.randomAnimalFunction;
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    EntityType<?> type = animalType.getRandomValue(context.getRandom());
    // prevent crash if empty, the config condition should handle this though
    if (type == null) {
      AnimalCrops.log.error("Received empty animal list for {}, a condition is missing in the loot table", type);
    } else {
      Utils.setEntityId(stack, Objects.requireNonNull(type.getRegistryName()).toString());
    }
    return stack;
  }

  private static class Serializer extends LootItemConditionalFunction.Serializer<RandomAnimalLootFunction> {
    @Override
    public void serialize(JsonObject json, RandomAnimalLootFunction randAnimal, JsonSerializationContext context) {
      super.serialize(json, randAnimal, context);
      json.addProperty("prop", randAnimal.type);
    }

    @Nonnull
    @Override
    public RandomAnimalLootFunction deserialize(JsonObject json, JsonDeserializationContext ctx, LootItemCondition[] conditions) {
      String type = GsonHelper.getAsString(json, "type").toLowerCase(Locale.ROOT);
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
