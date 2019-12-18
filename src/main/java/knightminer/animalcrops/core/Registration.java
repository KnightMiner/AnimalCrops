package knightminer.animalcrops.core;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.blocks.BlockAnimalCrops;
import knightminer.animalcrops.blocks.BlockAnimalLily;
import knightminer.animalcrops.items.ItemAnimalLily;
import knightminer.animalcrops.items.ItemAnimalSeeds;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@ObjectHolder(AnimalCrops.modID)
@EventBusSubscriber(modid = AnimalCrops.modID, bus = Bus.MOD)
public class Registration {
  public static final Block crops = injected(), lily = injected();
  public static final Block bush = injected();
  public static final ItemAnimalSeeds seeds = injected();
  @ObjectHolder("lily")
  public static final ItemAnimalSeeds lilySeeds = injected();
  @ObjectHolder("bush")
  public static final BlockItem itemBush = injected();
  @ObjectHolder("crops")
  public static final TileEntityType<TileAnimalCrops> cropsTE = injected();

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> r = event.getRegistry();

    register(r, new BlockAnimalCrops(), "crops");
    register(r, new BlockAnimalLily(), "lily");

    //if(Config.animalBush.get()) {
    //  register(r, new BlockAnimalBush(), "bush");
    //}
  }

  @SubscribeEvent
  public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
    IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

    register(r, TileEntityType.Builder.create(TileAnimalCrops::new, crops, lily).build(null), "crops");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> r = event.getRegistry();
    Item.Properties props = (new Item.Properties()).group(ItemGroup.MATERIALS);

    register(r, new ItemAnimalSeeds(crops, props), "seeds");
    register(r, new ItemAnimalLily(lily, props), "lily");

//    if(Config.animalBush.get()) {
//      register(r, new BlockItem(bush, props), "bush");
//    }
  }

  // anything with no register event
  @SubscribeEvent
  public static void registerMisc(FMLCommonSetupEvent event) {
    LootFunctionManager.registerFunction(new SetAnimalLootFunction.Serializer(new ResourceLocation(AnimalCrops.modID, "set_animal")));
  }


  /* Helper functions */

  /**
   * Helper method to get rid of warnings for object holder annotations
   * @param <T>
   * @return Null as required for Object Holder, but marked Nonnull to prevent null warnings
   */
  @Nonnull
  private static <T> T injected() {
    return null;
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
    value.setRegistryName(new ResourceLocation(AnimalCrops.modID, name));
    registry.register(value);
  }
}
