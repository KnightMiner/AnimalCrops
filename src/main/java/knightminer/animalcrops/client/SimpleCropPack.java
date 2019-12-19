package knightminer.animalcrops.client;

import com.google.common.collect.ImmutableSet;
import knightminer.animalcrops.AnimalCrops;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class SimpleCropPack extends ResourcePack {

  /** Base pack name, used for the folder containing pack resources */
  private static final String FOLDER = "simple_crops";
  /** Namespaced pack name, used as the internal name passed to the resource pack loader */
  public static final String PACK_NAME = AnimalCrops.modID + ":" + FOLDER;
  /** Resource prefix for valid pack resources. Essentially checks that they are client side Animal Crops resources */
  private static final String RES_PREFIX = ResourcePackType.CLIENT_RESOURCES.getDirectoryName() + "/" + AnimalCrops.modID + "/";
  /** Replacement prefix for pack resources, to load from FOLDER */
  private static final String PATH_PREFIX = String.format("/%s%s/", RES_PREFIX, FOLDER);

  /** Pack instance as it behaves the same way every time and does not require additional resources */
  private static final SimpleCropPack INSTANCE = new SimpleCropPack();

  /**
   * Default constructor, private as this functions properly singleton
   */
  private SimpleCropPack() {
    // this file is not used, so giving the most useful path
    super(new File(PATH_PREFIX));
  }

  @Nonnull
  @Override
  public String getName() {
    return "Simple Animal Crops";
  }

  @Override
  public Set<String> getResourceNamespaces(ResourcePackType type) {
    // only replace resources for animal crops
    return type == ResourcePackType.CLIENT_RESOURCES ? ImmutableSet.of(AnimalCrops.modID) : ImmutableSet.of();
  }

  @Override
  protected InputStream getInputStream(String name) throws IOException {
    // pack.mcmeta and pack.png are fetched without prefix, so pull from proper directory
    // everything else is prefixed, so prefix is trimmed
    if (name.equals("pack.mcmeta") || name.equals("pack.png") || name.startsWith(RES_PREFIX)) {
      return AnimalCrops.class.getResourceAsStream(getPath(name));
    }

    throw new ResourcePackFileNotFoundException(this.file, name);
  }

  @Override
  protected boolean resourceExists(String name) {
    if (!name.startsWith(RES_PREFIX)) {
      return false;
    }
    return AnimalCrops.class.getResource(getPath(name)) != null;
  }

  @Override
  public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String path, int maxDepth, Predicate<String> filter) {
    // this method appears to only be called for fonts, so just return an empty list
    return Collections.emptyList();
  }

  @Override
  public void close() throws IOException {
    // this function is called during game load and when the resource pack menu is opened and closed
    // so invalidate the loaded cache so we know whether to continue using the TESR
    isLoaded = null;
  }

  /**
   * Gets the resource path for a given resource
   * @param name  Default resource path
   * @return  Resource with the new path, either prefixed or with the old prefix replaced
   */
  private static String getPath(String name) {
    // if not prefixed with animalcrops, append the full path
    // used for pack.mcmeta and pack.png
    if (!name.startsWith(RES_PREFIX)) {
      return PATH_PREFIX + name;
    }

    // goes from assets/animalcrops/<res> to assets/animalcrops/simple_crop/<res>
    return PATH_PREFIX + name.substring(RES_PREFIX.length());
  }


  /* Static functions */

  /**
   * Implementaton of IPackFinder for the registration event
   * @param map      Resource pack map
   * @param factory  Pack info factory
   * @param <T>      Resource pack info type
   */
  public static <T extends ResourcePackInfo> void packFinder(Map<String,T> map, ResourcePackInfo.IFactory<T> factory) {
    map.put(PACK_NAME, ResourcePackInfo.createResourcePack(PACK_NAME, false, ()->INSTANCE, factory, ResourcePackInfo.Priority.TOP));
  }

  /** Cached state of the pack to check if it is loaded */
  private static Boolean isLoaded = null;

  /**
   * Checks if the simple crop pack is loaded, caching the result
   * @return  True if the pack is loaded, false otherwise
   */
  public static boolean isLoaded() {
    if (isLoaded == null) {
      isLoaded = Minecraft.getInstance().gameSettings.resourcePacks.contains(PACK_NAME);
    }

    return isLoaded;
  }
}
