package knightminer.animalcrops;

import knightminer.animalcrops.client.ClientEvents;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnimalCrops.modID)
public class AnimalCrops {
	public static final String modID = "animalcrops";
	public static final Logger log = LogManager.getLogger(modID);

	public AnimalCrops() {
		DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new ClientEvents());
		Utils.initReflection();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::configChanged);
	}

//	TODO: will probably ditch the animal bush entirely
//	@EventHandler
//	public void init(FMLInitializationEvent event) {
//		Config.init(event);
//
//		if(Config.animalBush && Config.animalBushChance > 0) {
//			GameRegistry.registerWorldGenerator(new WorldGenAnimalCrops(), 25);
//		}
//
//		proxy.init();
//	}
}
