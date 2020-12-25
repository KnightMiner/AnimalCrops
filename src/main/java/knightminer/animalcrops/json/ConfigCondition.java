package knightminer.animalcrops.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class ConfigCondition implements ILootCondition {
  private static final Map<String,ConfigCondition> PROPS = new HashMap<>();

  private final String name;
  private final BooleanSupplier supplier;
  private ConfigCondition(String name, BooleanSupplier supplier) {
    this.name = name;
    this.supplier = supplier;
  }

  @Override
  public boolean test(LootContext lootContext) {
    return supplier.getAsBoolean();
  }

  @Override
  public LootConditionType func_230419_b_() {
    return Registration.Loot.configCondition;
  }

  public static class Serializer implements ILootSerializer<ConfigCondition> {
    @Override
    public ConfigCondition deserialize(JsonObject json, JsonDeserializationContext context) {
      String prop = JSONUtils.getString(json, "prop");
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
    add("seeds", Config::dropAnimalSeeds);
    add("anemonemal", Config::dropAnemonemals);
    add("pollen", Config.dropAnimalPollen::get);
  }
}
