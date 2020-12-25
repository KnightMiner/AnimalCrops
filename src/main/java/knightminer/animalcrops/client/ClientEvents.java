package knightminer.animalcrops.client;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.tileentity.AnimalCropsTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ClientEvents {

	public ClientEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		Minecraft.getInstance().getResourcePackList().addPackFinder(SimpleCropPack::packFinder);

		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		if (manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)manager).addReloadListener(Settings.INSTANCE);
		}
	}

	@SubscribeEvent
	void registerTER(FMLClientSetupEvent event) {
		// this is bound unconditionally, but no-ops if the pack is disabled
		ClientRegistry.bindTileEntityRenderer(Registration.cropsTE, RenderAnimalCrops::new);

		// set render types to cutout
		RenderType cutout = RenderType.getCutout();
		RenderTypeLookup.setRenderLayer(Registration.crops, cutout);
		RenderTypeLookup.setRenderLayer(Registration.anemonemal, cutout);
		RenderTypeLookup.setRenderLayer(Registration.shrooms, cutout);
		RenderTypeLookup.setRenderLayer(Registration.magnemone, cutout);
	}

	@SubscribeEvent
	void registerBlockColors(ColorHandlerEvent.Block event) {
		// registered unconditionally as the resource pack may be added or removed later
		// besides, does not hurt to have an unused color handler
		event.getBlockColors().register((state, world, pos, index) -> {
			if (world == null || pos == null) {
				return -1;
			}
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof AnimalCropsTileEntity) {
				return getEggColor(te.getTileData(), index);
			}
			return -1;
		}, Registration.crops, Registration.anemonemal, Registration.shrooms, Registration.magnemone);
	}

	@SubscribeEvent
	void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, index) -> getEggColor(stack.getTag(), index),
																	 Registration.seeds, Registration.anemonemalSeeds, Registration.shrooms, Registration.magnemone);
	}


	/* Helper functions */

	/**
	 * Gets the egg color for the given NBT
	 * @param tags   NBT, from either a item stack or a tile entiy
	 * @param index  Tint index to use
	 * @return  Egg color for the given tags and index
	 */
	@SuppressWarnings("Convert2MethodRef")
	private static int getEggColor(@Nullable CompoundNBT tags, int index) {
		return Utils.getEntityID(tags)
								.flatMap(loc -> EntityType.byKey(loc))
								.map(SpawnEggItem::getEgg)
								.map((egg)->egg.getColor(index))
								.orElse(-1);
	}
}
