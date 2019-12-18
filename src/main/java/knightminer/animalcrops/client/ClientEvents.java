package knightminer.animalcrops.client;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

public class ClientEvents {
	private static final ResourceLocation CROPS_EGG = new ResourceLocation(AnimalCrops.modID, "crops_egg");
	private static final ResourceLocation LILY_EGG = new ResourceLocation(AnimalCrops.modID, "lily_egg");

	public ClientEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void registerTER(FMLClientSetupEvent event) {
		if(Config.fancyCropRendering.get()) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileAnimalCrops.class, new RenderAnimalCrops());
		}
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		// add the replacement models if requested
		if(!Config.fancyCropRendering.get()) {
			ModelLoader.addSpecialModel(CROPS_EGG);
			ModelLoader.addSpecialModel(LILY_EGG);
		}
	}

	@SubscribeEvent
	public void swapModels(ModelBakeEvent event) {
		Map<ResourceLocation,IBakedModel> map = event.getModelRegistry();
		if(!Config.fancyCropRendering.get()) {
			map.put(Registration.crops.getRegistryName(), map.get(CROPS_EGG));
			map.put(Registration.lily.getRegistryName(), map.get(LILY_EGG));
		}
	}

	private static int getEggColor(CompoundNBT tags, int index) {
		return Utils.getEntityID(tags)
								.flatMap(EntityType::byKey)
								.map(SpawnEggItem::getEgg)
								.map((egg)->egg.getColor(index))
								.orElse(-1);
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		// when not fancy, we have a colored egg in the middle
		if(!Config.fancyCropRendering.get()) {
			event.getBlockColors().register((state, world, pos, index) -> {
				if((index == 0 || index == 1) && state.get(CropsBlock.AGE) > 1) {
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof TileAnimalCrops) {
						return getEggColor(te.getTileData(), index);
					}
				}
				return -1;
			}, Registration.crops, Registration.lily);
		}
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, index) -> getEggColor(stack.getTag(), index),
																	 Registration.seeds, Registration.lilySeeds);
	}
}
