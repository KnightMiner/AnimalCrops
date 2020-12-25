package knightminer.animalcrops.core;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.blocks.AnemonemalBlock;
import knightminer.animalcrops.blocks.AnimalCropsBlock;
import knightminer.animalcrops.items.AnimalPollenItem;
import knightminer.animalcrops.items.AnimalSeedsItem;
import knightminer.animalcrops.json.ConfigCondition;
import knightminer.animalcrops.json.RandomAnimalLootFunction;
import knightminer.animalcrops.json.SetAnimalLootFunction;
import knightminer.animalcrops.tileentity.AnimalCropsTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings("unused")
@ObjectHolder(AnimalCrops.modID)
@EventBusSubscriber(modid = AnimalCrops.modID, bus = Bus.MOD)
public class Registration {
  public static final Block crops = injected(), anemonemal = injected();
  public static final AnimalSeedsItem seeds = injected();
  @ObjectHolder("anemonemal")
  public static final AnimalSeedsItem anemonemalSeeds = injected();
  public static final Item pollen = injected();
  @ObjectHolder("crops")
  public static final TileEntityType<AnimalCropsTileEntity> cropsTE = injected();

  // subclass to prevent conflict with non-Forge registries and object holder
  public static class Loot {
    public static LootFunctionType setAnimalFunction;
    public static LootFunctionType randomAnimalFunction;
    public static LootConditionType configCondition;
  }

  @SubscribeEvent
  static void registerBlocks(RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> r = event.getRegistry();

    Block.Properties props = Block.Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(0).sound(SoundType.CROP).doesNotBlockMovement();
    register(r, new AnimalCropsBlock(props, Config.animalCrops::get), "crops");
    register(r, new AnemonemalBlock(props, Config.anemonemals::get), "anemonemal");
  }

  @SubscribeEvent
  static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
    IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

    //noinspection ConstantConditions
    register(r, TileEntityType.Builder.create(AnimalCropsTileEntity::new, crops, anemonemal).build(null), "crops");
  }

  @SubscribeEvent
  static void registerItems(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> r = event.getRegistry();
    Item.Properties props = (new Item.Properties()).group(ItemGroup.MATERIALS);

    register(r, new AnimalSeedsItem(crops, props), "seeds");
    register(r, new AnimalSeedsItem(anemonemal, props), "anemonemal");
    register(r, new AnimalPollenItem(props), "pollen");
  }

  // anything with no register event
  @SubscribeEvent
  static void registerMisc(FMLCommonSetupEvent event) {
    ComposterBlock.registerCompostable(0.5f, seeds);
    ComposterBlock.registerCompostable(0.5f, anemonemalSeeds);
    ComposterBlock.registerCompostable(0.5f, pollen);

    Loot.setAnimalFunction = register(Registry.LOOT_FUNCTION_TYPE, "set_animal", new LootFunctionType(new SetAnimalLootFunction.Serializer()));
    Loot.randomAnimalFunction = register(Registry.LOOT_FUNCTION_TYPE, "random_animal", new LootFunctionType(new RandomAnimalLootFunction.Serializer()));
    Loot.configCondition = register(Registry.LOOT_CONDITION_TYPE, "config", new LootConditionType(new ConfigCondition.Serializer()));
  }

  // registered to FORGE event bus in AnimalCrops
  public static void missingBlockMappings(MissingMappings<Block> event) {
    for (Mapping<Block> mapping : event.getAllMappings()) {
      if (AnimalCrops.modID.equals(mapping.key.getNamespace()) && "lily".equals(mapping.key.getPath())) {
        mapping.ignore();
      }
    }
  }

  // registered to FORGE event bus in AnimalCrops
  public static void missingItemMappings(MissingMappings<Item> event) {
    for (Mapping<Item> mapping : event.getAllMappings()) {
      if (AnimalCrops.modID.equals(mapping.key.getNamespace()) && "lily".equals(mapping.key.getPath())) {
        mapping.remap(anemonemalSeeds);
      }
    }
  }

  // registered to FORGE event bus in AnimalCrops
  public static void injectLoot(LootTableLoadEvent event) {
    addToBlockLoot(event, Blocks.GRASS);
    addToBlockLoot(event, Blocks.TALL_GRASS);
    addToBlockLoot(event, Blocks.FERN);
    addToBlockLoot(event, Blocks.LARGE_FERN);
    addToBlockLoot(event, Blocks.SEAGRASS);
    addToBlockLoot(event, Blocks.TALL_SEAGRASS);
  }


  /* Helper functions */

  /**
   * Helper method to get rid of warnings for object holder annotations
   * @return Null as required for Object Holder, but marked Nonnull to prevent null warnings
   */
  @SuppressWarnings("ConstantConditions")
  @Nonnull
  private static <T> T injected() {
    return null;
  }

  /**
   * Gets a resource location in the animal crops domain
   * @param name  Resource path
   * @return  Animal Crops resource location
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(AnimalCrops.modID, name);
  }

  /**
   * Helper method to register an entry, setting the registry name
   * @param registry  Registry to use
   * @param value     Value to register
   * @param name      Registry name, will be namespaced under AnimalCrops
   * @param <V>  Value type
   * @param <T>  Registry type
   */
  private static <V extends T, T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, V value, String name) {
    value.setRegistryName(getResource(name));
    registry.register(value);
  }

  private static <T> T register(Registry<? super T> registry, String name, T value) {
    return Registry.register(registry, getResource(name), value);
  }

  /**
   * Injects an custom loot pool from animalcrops:blocks/minecraft/ into the vanilla block
   * @param event  Event, used to determine if this is the proper loot table
   * @param block  Block to inject loot into
   */
  private static void addToBlockLoot(LootTableLoadEvent event, Block block) {
    String name = Objects.requireNonNull(block.getRegistryName()).getPath();
    if (!event.getName().getNamespace().equals("minecraft") || !event.getName().getPath().equals("blocks/" + name)) {
      return;
    }
    LootTable table = event.getTable();
    if (table != LootTable.EMPTY_LOOT_TABLE) {
      ResourceLocation location = getResource("blocks/minecraft/" + name);
      table.addPool(new LootPool.Builder().name(location.toString()).rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(location)).build());
    }
  }
}
