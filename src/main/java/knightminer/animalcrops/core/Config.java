package knightminer.animalcrops.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {

	public static List<ResourceLocation> animals = Collections.emptyList();
	private static String[] animalDefaults = {
			"minecraft:chicken",
			"minecraft:cow",
			"minecraft:donkey",
			"minecraft:horse",
			"minecraft:llama",
			"minecraft:mooshroom",
			"minecraft:ocelot",
			"minecraft:parrot",
			"minecraft:pig",
			"minecraft:polar_bear",
			"minecraft:rabbit",
			"minecraft:sheep",
			"minecraft:villager",
			"minecraft:wolf",
	};
	public static boolean canBonemeal = true;
	public static boolean fancyCropRendering = true;

	static Configuration configFile;
	public static void preInit(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		canBonemeal = configFile.getBoolean("canBonemeal", "general", canBonemeal,
				"Determines if bonemeal can be applied to the animal crop");
		fancyCropRendering = configFile.getBoolean("fancyCropRendering", "client", fancyCropRendering,
				"Makes the animal crop render the entity model. If false will just render a tinted texture based on the spawn egg colors");

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	public static void init(FMLInitializationEvent event) {
		animalDefaults = configFile.get("general", "animals", animalDefaults,
				"List of animals to add as animal seeds. Must extend EntityAgeable (basically passive mobs and villagers)").getStringList();

		// ensure all the animals are valid
		animals = new ArrayList<>();
		for(String animal : animalDefaults) {
			ResourceLocation location = new ResourceLocation(animal);
			if(EntityList.isRegistered(location)) {
				animals.add(location);
			}
		}

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

}
