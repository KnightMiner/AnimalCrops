package knightminer.animalcrops.client;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ClientEvents {

	public ClientEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		Minecraft minecraft = Minecraft.getInstance();
		//noinspection ConstantConditions  datagen its null
		if (minecraft != null) {
			minecraft.getResourcePackRepository().addPackFinder(SimpleCropPack.INSTANCE);
			ResourceManager manager = Minecraft.getInstance().getResourceManager();
			if (manager instanceof ReloadableResourceManager reloadable) {
				reloadable.registerReloadListener(Settings.INSTANCE);
			}
		}
	}

	@SubscribeEvent
	void registerTER(FMLClientSetupEvent event) {
		// this is bound unconditionally, but no-ops if the pack is disabled
		BlockEntityRenderers.register(Registration.cropsTE, RenderAnimalCrops::new);

		// set render types to cutout
		RenderType cutout = RenderType.cutout();
		ItemBlockRenderTypes.setRenderLayer(Registration.crops, cutout);
		ItemBlockRenderTypes.setRenderLayer(Registration.anemonemal, cutout);
		ItemBlockRenderTypes.setRenderLayer(Registration.shrooms, cutout);
		ItemBlockRenderTypes.setRenderLayer(Registration.magnemone, cutout);
	}

	@SubscribeEvent
	void registerBlockColors(ColorHandlerEvent.Block event) {
		// registered unconditionally as the resource pack may be added or removed later
		// besides, does not hurt to have an unused color handler
		event.getBlockColors().register((state, world, pos, index) -> {
			if (world == null || pos == null) {
				return -1;
			}
			BlockEntity te = world.getBlockEntity(pos);
			if (te != null) {
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
	private static int getEggColor(@Nullable CompoundTag tags, int index) {
		return Utils.getEntityID(tags)
								.flatMap(loc -> EntityType.byString(loc))
								.map(ForgeSpawnEggItem::fromEntityType)
								.map(egg->egg.getColor(index))
								.orElse(-1);
	}
}
