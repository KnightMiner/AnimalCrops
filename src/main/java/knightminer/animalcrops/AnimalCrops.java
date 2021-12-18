package knightminer.animalcrops;

import knightminer.animalcrops.client.ClientEvents;
import knightminer.animalcrops.core.AnimalTags;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.datagen.BlockTagProvider;
import knightminer.animalcrops.datagen.EntityTypeTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnimalCrops.modID)
public class AnimalCrops {
	public static final String modID = "animalcrops";
	public static final Logger log = LogManager.getLogger(modID);
	public AnimalCrops() {
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->ClientEvents::new);
		Utils.initReflection();
		AnimalTags.init();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(Config::configChanged);
		modBus.addListener(AnimalCrops::onDatagen);
	}

	/** Called to add datagenerators */
	private static void onDatagen(GatherDataEvent event) {
		if (event.includeServer()) {
			DataGenerator generator = event.getGenerator();
			ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
			generator.addProvider(new BlockTagProvider(generator, existingFileHelper));
			generator.addProvider(new EntityTypeTagProvider(generator, existingFileHelper));
		}
	}
}
