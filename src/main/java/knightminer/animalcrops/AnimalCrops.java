package knightminer.animalcrops;

import knightminer.animalcrops.client.ClientEvents;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnimalCrops.modID)
public class AnimalCrops {
	public static final String modID = "animalcrops";
	public static final Logger log = LogManager.getLogger(modID);

	public AnimalCrops() {
		DistExecutor.callWhenOn(Dist.CLIENT, ()->ClientEvents::new);
		Utils.initReflection();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
	}
}
