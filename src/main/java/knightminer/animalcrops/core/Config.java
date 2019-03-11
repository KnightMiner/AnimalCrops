package knightminer.animalcrops.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {

	public static List<ResourceLocation> animals = Collections.emptyList();
	public static List<ResourceLocation> seaAnimals = Collections.emptyList();
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
			"waddles:adelie_penguin",
	};
	private static String[] seaAnimalDefaults = {
			"minecraft:squid",
	};

	// crop
	public static boolean canBonemeal = true;
	public static boolean fancyCropRendering = true;
	public static int seedDropChance = 0;
	// bush
	public static boolean rightClickHarvest = true;
	public static boolean animalBush = true;
	public static int animalBushChance = 20;

	static Configuration configFile;
	public static void preInit(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		// crop
		configFile.moveProperty("general", "canBonemeal", "crop");
		configFile.moveProperty("general", "rightClickHarvest", "crop");
		canBonemeal = configFile.getBoolean("canBonemeal", "crop", canBonemeal,
				"Determines if bonemeal can be applied to the animal crop");
		rightClickHarvest = configFile.getBoolean("rightClickHarvest", "crop", rightClickHarvest,
				"Harvests the crop on right click (which is really the same as just breaking it). Added because people cannot write their right click harvest mods right.");
		seedDropChance = configFile.getInt("seedDropChance", "crop", seedDropChance, 0, 100,
				"Chance for an animal crop to drop a seed if fully grown in addition to the animal. Formula is a 1 in <chance> chance of dropping. Set to 0 to never drop seeds, and 1 to always drop");

		// bush
		configFile.moveProperty("general", "animalBush", "bush");
		configFile.renameProperty("bush", "animalBush", "enable");
		configFile.moveProperty("general", "animalBushChance", "bush");
		configFile.renameProperty("bush", "animalBushChance", "chance");
		animalBush = configFile.getBoolean("enable", "bush", animalBush,
				"Adds the animal bush: a block that when broken drops a random animal seed.");
		animalBushChance = configFile.getInt("chance", "bush", animalBushChance, 0, 500,
				"Chance for an animal bush to generate per chunk. Formula is a 1 in <chance> chance of generating. Set to 0 to disable generation.");

		// client
		fancyCropRendering = configFile.getBoolean("fancyCropRendering", "client", fancyCropRendering,
				"Makes the animal crop render the entity model. If false will just render a tinted texture based on the spawn egg colors");

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	private static List<ResourceLocation> processAnimals(String[] animals) {
		List<ResourceLocation> result = new ArrayList<>();
		for(String animal : animals) {
			// ensure the entity is registered
			ResourceLocation location = new ResourceLocation(animal);
			if(!EntityList.ENTITY_EGGS.containsKey(location)) {
				AnimalCrops.log.error("Invalid entity {}, must have a spawn egg", animal);
				continue;
			}
			if(EntityList.isRegistered(location)) {
				// insure the entity type is valid, we only allow entity living
				if(EntityLiving.class.isAssignableFrom(EntityList.getClass(location))) {
					result.add(location);
				} else {
					AnimalCrops.log.error("Invalid entity type for {}, must extend EntityLiving", animal);
				}
			} else {
				AnimalCrops.log.debug("Could not find entity {}, either entity is missing or the ID is incorrect", animal);
			}
		}
		return result;
	}

	public static void init(FMLInitializationEvent event) {
		animalDefaults = configFile.get("general", "animals", animalDefaults,
				"List of animals to add as animal seeds. Must extend EntityLiving").getStringList();
		seaAnimalDefaults = configFile.get("general", "seaAnimals", seaAnimalDefaults,
				"List of water animals to add as animal lilys. Expected to be water based mobs").getStringList();

		// validate animal lists and process into resource locations
		animals = processAnimals(animalDefaults);
		seaAnimals = processAnimals(seaAnimalDefaults);

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

}
