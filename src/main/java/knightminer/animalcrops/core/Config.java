package knightminer.animalcrops.core;

import com.google.common.collect.ImmutableList;
import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class Config {

	public static final Builder SERVER_BUILDER;
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final Builder CLIENT_BUILDER;
	public static final ForgeConfigSpec CLIENT_SPEC;

	public static ConfigValue<List<? extends String>> animalCrops;
	private static final List<String> ANIMAL_CROP_DEFAULTS = ImmutableList.of(
			"minecraft:cat",
			"minecraft:chicken",
			"minecraft:cow",
			"minecraft:donkey",
			"minecraft:fox",
			"minecraft:horse",
			"minecraft:llama",
			"minecraft:mooshroom",
			"minecraft:ocelot",
			"minecraft:panda",
			"minecraft:parrot",
			"minecraft:pig",
			"minecraft:polar_bear",
			"minecraft:rabbit",
			"minecraft:sheep",
			"minecraft:villager",
			"minecraft:wolf",
			"waddles:adelie_penguin");
	public static ConfigValue<List<? extends String>> animalLilies;
	private static final List<String> ANIMAL_LILY_DEFAULTS = ImmutableList.of(
			"minecraft:cod",
			"minecraft:dolphin",
			"minecraft:pufferfish",
			"minecraft:salmon",
			"minecraft:squid",
			"minecraft:tropical_fish",
			"minecraft:turtle");

	// crop
	public static BooleanValue canBonemeal;
	public static BooleanValue fancyCropRendering;
	// bush
	public static BooleanValue animalBush;
	public static IntValue animalBushChance;

	static {
		SERVER_BUILDER = new Builder();
		CLIENT_BUILDER = new Builder();
		configure(SERVER_BUILDER, CLIENT_BUILDER);
		SERVER_SPEC = SERVER_BUILDER.build();
		CLIENT_SPEC = CLIENT_BUILDER.build();
	}


	private static void configure(Builder server, Builder client) {
		// crop
		server.push("crop");
		{
			canBonemeal = server
					.comment("Determines if bonemeal can be applied to the animal crop")
					.define("bonemeal", false);
			animalCrops = server
					.comment("List of animals to add as animal seeds. Must extend MobEntity and have a spawn egg.")
					.defineList("animalCrops", ANIMAL_CROP_DEFAULTS, Config::validateAnimal);
			animalLilies = server
					.comment("List of water animals to add as animal lilies. Must extend MobEntity and have a spawn egg.")
					.defineList("animalLilies", ANIMAL_LILY_DEFAULTS, Config::validateAnimal);
		}
		server.pop();

		// bush
//		server.push("bush");
//		{
//			animalBush = server
//					.comment("Adds the animal bush: a block that when broken drops a random animal seed.")
//					.define("enable", true);
//			animalBushChance = server
//					.comment("Chance for an animal bush to generate per chunk. Formula is a 1 in <chance> chance of generating. Set to 0 to disable generation.")
//					.defineInRange("chance", 2, 0, 500);
//		}
//		server.pop();

		// client
		fancyCropRendering = client
				.comment("Makes the animal crop render the entity model. If false will just render a tinted texture based on the spawn egg colors")
				.define("fancyCropRendering", true);
	}

	public static void configChanged(final ModConfig.ModConfigEvent configEvent) {
//
//		// validate animal lists and process into resource locations
//		animals = processAnimals(animalDefaults);
//		seaAnimals = processAnimals(seaAnimalDefaults);

	}

	/**
	 * Validates that all defaults are valid
	 * @param obj
	 * @return
	 */
	private static boolean validateAnimal(Object obj) {
		if (!(obj instanceof String)) {
			return false;
		}

		// must be a valid entity
		String animal = (String)obj;
		EntityType<?> type = EntityType.byKey(animal).orElse(null);
		if (type == null) {
			AnimalCrops.log.error("Invalid entity {}, cannot find entity", animal);
			return false;
		}

		// must have a spawn egg, use that for colors
		SpawnEggItem item = SpawnEggItem.getEgg(type);
		if(item == null) {
			AnimalCrops.log.error("Invalid entity {}, must have a spawn egg", animal);
			return false;
		}

		// insure the entity type is valid, we only allow entity living
//		if(!MobEntity.class.isAssignableFrom(type.getClass())) {
//			AnimalCrops.log.error("Invalid entity {}, must extend MobEntity", animal);
//			return false;
//		}

		return true;
	}
}
