package knightminer.animalcrops.client;

import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEvents {

	public ClientEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		Minecraft.getInstance().getResourcePackList().addPackFinder(SimpleCropPack::packFinder);
	}

	@SubscribeEvent
	public void registerTER(FMLClientSetupEvent event) {
		// this is bound unconditionally, but no-ops if the pack is disabled
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnimalCrops.class, new RenderAnimalCrops());
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		// registered unconditionally as the resource pack may be added or removed later
		// besides, does not hurt to have an unused color handler
		event.getBlockColors().register((state, world, pos, index) -> {
			if (world == null || pos == null) {
				return -1;
			}
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileAnimalCrops) {
				return getEggColor(te.getTileData(), index);
			}
			return -1;
		}, Registration.crops, Registration.lily);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, index) -> getEggColor(stack.getTag(), index),
																	 Registration.seeds, Registration.lilySeeds);
	}


	/* Helper functions */

	/**
	 * Gets the egg color for the given NBT
	 * @param tags   NBT, from either a item stack or a tile entiy
	 * @param index  Tint index to use
	 * @return  Egg color for the given tags and index
	 */
	private static int getEggColor(CompoundNBT tags, int index) {
		return Utils.getEntityID(tags)
								.flatMap(EntityType::byKey)
								.map(SpawnEggItem::getEgg)
								.map((egg)->egg.getColor(index))
								.orElse(-1);
	}
}
