package knightminer.animalcrops.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Settings implements ResourceManagerReloadListener {

  /** Location of the settings file */
  private static final ResourceLocation LOCATION = new ResourceLocation(AnimalCrops.modID, "models/settings.json");
  /** JSON key for the properties */
  private static final String CROP_ENTITY_KEY = "render_crop_entity";
  private static final String ANEMONEMAL_ENTITY_KEY = "render_anemonemal_entity";
  private static final String SHROOM_ENTITY_KEY = "render_shroom_entity";
  private static final String MAGNEMONE_ENTITY_KEY = "render_magnemone_entity";

  // current settings
  private boolean renderCropEntity = true;
  private boolean renderAnemonemalEntity = true;
  private boolean renderShroomEntity = true;
  private boolean renderMagnemoneEntity = true;

  public static final Settings INSTANCE = new Settings();
  private Settings() {}

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    // model type as the TESR is linked to the blockstate models
      // first, get a list of all json files
      List<JsonObject> jsonFiles;
      try {
        jsonFiles = manager.getResources(LOCATION).stream().map(Settings::getJson).filter(Objects::nonNull).collect(Collectors.toList());
      } catch(IOException e) {
        jsonFiles = Collections.emptyList();
        AnimalCrops.log.error("Failed to load model settings file", e);
      }

      // then grab the relevant settings values from the booleans
      renderCropEntity       = getTopBoolean(jsonFiles, CROP_ENTITY_KEY, true);
      renderAnemonemalEntity = getTopBoolean(jsonFiles, ANEMONEMAL_ENTITY_KEY, true);
      renderShroomEntity     = getTopBoolean(jsonFiles, SHROOM_ENTITY_KEY, true);
      renderMagnemoneEntity  = getTopBoolean(jsonFiles, MAGNEMONE_ENTITY_KEY, true);
  }


  /* Helpers */

  /**
   * Gets the top level pack boolean from the JSON object list
   * @param list  List of json objects
   * @param key   Key to fetch from JSON
   * @param def   Default value
   * @return  Value from top level pack, or null if no pack has it defined
   */
  @SuppressWarnings("SameParameterValue")
  private static boolean getTopBoolean(@Nonnull List<JsonObject> list, @Nonnull String key, boolean def) {
    // for some reason, getAllResources puts the top most pack at the end of the list, so search in reverse order
    for(int i = list.size() - 1; i >= 0; --i) {
      JsonObject json = list.get(i);
      if (GsonHelper.isBooleanValue(json, key)) {
        return GsonHelper.getAsBoolean(json, key);
      }
    }
    return def;
  }

  /**
   * Converts the resource into a JSON file
   * @param resource  Resource to read. Closed when done
   * @return  JSON object, or null if failed to parse
   */
  @Nullable
  private static JsonObject getJson(Resource resource) {
    // this code is heavily based on ResourcePack::getResourceMetadata
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      return GsonHelper.parse(reader);
    } catch (JsonParseException | IOException e) {
      AnimalCrops.log.error("Failed to load model settings file", e);
      return null;
    }
  }


  /* Static methods */

  /**
   * Method to check if these crop entites should render
   * @param block  Block being rendered
   * @return True if this block should render the TER entity
   */
  public static boolean shouldRenderEntity(Block block) {
    if (block == Registration.crops) {
      return INSTANCE.renderCropEntity;
    }
    if (block == Registration.anemonemal) {
      return INSTANCE.renderAnemonemalEntity;
    }
    if (block == Registration.shrooms) {
      return INSTANCE.renderShroomEntity;
    }
    if (block == Registration.magnemone) {
      return INSTANCE.renderMagnemoneEntity;
    }
    // fallback in case some other mod extends this
    return true;
  }
}
