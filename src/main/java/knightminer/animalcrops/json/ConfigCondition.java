package knightminer.animalcrops.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Conditions a loot table entry based on config
 */
public class ConfigCondition implements LootItemCondition {
  private static final Map<String,ConfigCondition> PROPS = new HashMap<>();
  public static final ConfigSerializer SERIALIZER = new ConfigSerializer();

  private final String name;
  private final BooleanSupplier supplier;
  private ConfigCondition(String name, BooleanSupplier supplier) {
    this.name = name;
    this.supplier = supplier;
  }

  @Override
  public LootItemConditionType getType() {
    return Registration.Loot.configCondition;
  }

  @Override
  public boolean test(LootContext lootContext) {
    return supplier.getAsBoolean();
  }

  public static class ConfigSerializer implements Serializer<ConfigCondition> {
    @Override
    public ConfigCondition deserialize(JsonObject json, JsonDeserializationContext context) {
      String prop = GsonHelper.getAsString(json, "prop");
      ConfigCondition config = PROPS.get(prop.toLowerCase(Locale.ROOT));
      if (config == null) {
        throw new JsonSyntaxException("Invalid config property name '" + prop + "'");
      }
      return config;
    }

    @Override
    public void serialize(JsonObject json, ConfigCondition config, JsonSerializationContext context) {
      json.addProperty("prop", config.name);
    }
  }

  /* Setup prop list */
  private static void add(String name, BooleanSupplier supplier) {
    PROPS.put(name, new ConfigCondition(name, supplier));
  }

  static {
    add("seeds", Config.animalCrops::doesDrop);
    add("anemonemal", Config.anemonemals::doesDrop);
    add("shrooms", Config.animalShrooms::doesDrop);
    add("magnemone", Config.magnemones::doesDrop);
    add("pollen", Config.dropAnimalPollen::get);
  }
}
