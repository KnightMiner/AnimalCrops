package knightminer.animalcrops.core;

import javax.annotation.Nonnull;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.client.RenderAnimalCrops;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	private static final ResourceLocation CROPS_EGG = new ResourceLocation(AnimalCrops.modID, "crops_egg");

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(AnimalCrops.seeds, 0, new ModelResourceLocation(AnimalCrops.seeds.getRegistryName(), "inventory"));

		// fancy means we show the entity model, regular we show an egg
		if(Config.fancyCropRendering) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileAnimalCrops.class, new RenderAnimalCrops());
		} else {
			ModelLoader.setCustomStateMapper(AnimalCrops.crops, new NameStateMapper(CROPS_EGG));
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		// when not fancy, we have a colored egg in the middle
		if(!Config.fancyCropRendering) {
			event.getBlockColors().registerBlockColorHandler((state, world, pos, index) -> {
				if((index == 0 || index == 1) && state.getValue(BlockCrops.AGE) > 1) {
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof TileAnimalCrops) {
			            EntityEggInfo egg = EntityList.ENTITY_EGGS.get(Utils.getEntityID(te.getTileData()));
			            if (egg != null) {
			                return index == 0 ? egg.primaryColor : egg.secondaryColor;
			            }
					}
				}
				return -1;
			}, AnimalCrops.crops);
		}
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().registerItemColorHandler((stack, index) -> {
			// only use first two indexes
			if(index == 0 || index == 1) {
				// grab egg info and use to color
	            EntityEggInfo egg = EntityList.ENTITY_EGGS.get(Utils.getEntityID(stack.getTagCompound()));
	            if (egg != null) {
	                return index == 0 ? egg.primaryColor : egg.secondaryColor;
	            }
			}

			return -1;
		}, AnimalCrops.seeds);
	}


	public class NameStateMapper extends StateMapperBase {
		private ResourceLocation name;
		public NameStateMapper(ResourceLocation name) {
			this.name = name;
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			return new ModelResourceLocation(name, this.getPropertyString(state.getProperties()));
		}
	}
}
