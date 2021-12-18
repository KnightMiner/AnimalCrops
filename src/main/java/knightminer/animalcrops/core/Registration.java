package knightminer.animalcrops.core;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.blocks.AnemonemalBlock;
import knightminer.animalcrops.blocks.AnimalCropsBlock;
import knightminer.animalcrops.blocks.AnimalShroomBlock;
import knightminer.animalcrops.blocks.entity.AnimalCropsBlockEntity;
import knightminer.animalcrops.items.AnimalPollenItem;
import knightminer.animalcrops.items.AnimalSeedsItem;
import knightminer.animalcrops.json.AddEntryLootModifier;
import knightminer.animalcrops.json.ConfigCondition;
import knightminer.animalcrops.json.RandomAnimalLootFunction;
import knightminer.animalcrops.json.SetAnimalLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@ObjectHolder(AnimalCrops.modID)
@EventBusSubscriber(modid = AnimalCrops.modID, bus = Bus.MOD)
public class Registration {
  public static final Block crops = injected(), anemonemal = injected(), shrooms = injected(), magnemone = injected();

  public static final AnimalSeedsItem seeds = injected(), spores = injected();
  @ObjectHolder("anemonemal")
  public static final AnimalSeedsItem anemonemalSeeds = injected();
  @ObjectHolder("magnemone")
  public static final AnimalSeedsItem magnemoneSpores = injected();

  public static final Item pollen = injected();
  @ObjectHolder("crops")
  public static final BlockEntityType<AnimalCropsBlockEntity> cropsTE = injected();

  // subclass to prevent conflict with non-Forge registries and object holder
  public static class Loot {
    public static LootItemFunctionType setAnimalFunction;
    public static LootItemFunctionType randomAnimalFunction;
    public static LootItemConditionType configCondition;
  }

  @SubscribeEvent
  static void registerBlocks(RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> r = event.getRegistry();

    BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.PLANT).randomTicks().strength(0).sound(SoundType.CROP).noCollission();
    register(r, new AnimalCropsBlock(props, AnimalTags.ANIMAL_CROPS), "crops");
    register(r, new AnemonemalBlock(props, AnimalTags.ANEMONEMAL, () -> Fluids.WATER, FluidTags.WATER), "anemonemal");
    props = BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED).randomTicks().strength(0).sound(SoundType.NETHER_WART).noCollission();
    register(r, new AnimalShroomBlock(props, AnimalTags.ANIMAL_SHROOMS), "shrooms");
    register(r, new AnemonemalBlock(props, AnimalTags.MAGNEMONES, () -> Fluids.LAVA, FluidTags.LAVA), "magnemone");
  }

  @SubscribeEvent
  static void registerTE(RegistryEvent.Register<BlockEntityType<?>> event) {
    IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();

    //noinspection ConstantConditions
    register(r, BlockEntityType.Builder.of(AnimalCropsBlockEntity::new, crops, anemonemal, shrooms, magnemone).build(null), "crops");
  }

  @SubscribeEvent
  static void registerItems(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> r = event.getRegistry();
    Item.Properties props = (new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS);

    register(r, new AnimalSeedsItem(crops, props), "seeds");
    register(r, new AnimalSeedsItem(anemonemal, props), "anemonemal");
    register(r, new AnimalSeedsItem(shrooms, props), "spores");
    register(r, new AnimalSeedsItem(magnemone, props), "magnemone");
    register(r, new AnimalPollenItem(props), "pollen");
  }

  @SubscribeEvent
  static void registerGlobalLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
    IForgeRegistry<GlobalLootModifierSerializer<?>> r = event.getRegistry();
    register(r, new AddEntryLootModifier.Serializer(), "add_entry");
  }

  // anything with no register event
  @SubscribeEvent
  static void registerMisc(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ComposterBlock.COMPOSTABLES.put(seeds, 0.5f);
      ComposterBlock.COMPOSTABLES.put(anemonemalSeeds, 0.5f);
      ComposterBlock.COMPOSTABLES.put(spores, 0.5f);
      ComposterBlock.COMPOSTABLES.put(magnemoneSpores, 0.5f);
      ComposterBlock.COMPOSTABLES.put(pollen, 0.5f);
    });

    Loot.setAnimalFunction = register(Registry.LOOT_FUNCTION_TYPE, "set_animal", new LootItemFunctionType(SetAnimalLootFunction.SERIALIZER));
    Loot.randomAnimalFunction = register(Registry.LOOT_FUNCTION_TYPE, "random_animal", new LootItemFunctionType(RandomAnimalLootFunction.SERIALIZER));
    Loot.configCondition = register(Registry.LOOT_CONDITION_TYPE, "config", new LootItemConditionType(ConfigCondition.SERIALIZER));
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

  /**
   * Registers a value to a vanilla registry
   * @param registry  Registry instance
   * @param name      Name to register
   * @param value     Value to register
   * @param <T>       Value type
   * @return  Registered value
   */
  private static <T> T register(Registry<? super T> registry, String name, T value) {
    return Registry.register(registry, getResource(name), value);
  }
}
