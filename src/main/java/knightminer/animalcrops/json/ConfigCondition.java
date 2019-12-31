package knightminer.animalcrops.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.animalcrops.core.Config;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

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

  public static class Serializer extends ILootCondition.AbstractSerializer<ConfigCondition> {
    public Serializer(ResourceLocation location) {
      super(location, ConfigCondition.class);
    }

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
