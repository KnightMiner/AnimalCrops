package knightminer.animalcrops.core;

import com.google.common.collect.ImmutableList;
import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Config {

	private static final Builder BUILDER;
	public static final ForgeConfigSpec SPEC;

	// crop
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
	public static BooleanValue canBonemeal;

	static {
		BUILDER = new Builder();
		configure(BUILDER);
		SPEC = BUILDER.build();
	}

	private static void configure(Builder builder) {
		// crop
		builder.push("crop");
		{
			canBonemeal = builder
					.comment("Determines if bonemeal can be applied to the animal crop")
					.define("bonemeal", false);
			animalCrops = builder
					.comment("List of animals to add as animal seeds. Must extend MobEntity and have a spawn egg.")
					.defineList("animalCrops", validateDefaults(ANIMAL_CROP_DEFAULTS), Config::validateAnimal);
			animalLilies = builder
					.comment("List of water animals to add as animal lilies. Must extend MobEntity and have a spawn egg.")
					.defineList("animalLilies", validateDefaults(ANIMAL_LILY_DEFAULTS), Config::validateAnimal);
		}
		builder.pop();
	}

	/**
	 * Creates a callback giving default list items
	 * @param defaults  Default defaults, may contain mobs from unloaded mods
	 * @return Validated defaults
	 */
	private static Supplier<List<? extends String>> validateDefaults(List<String> defaults) {
		return () -> defaults.stream().filter(Config::validateAnimal).collect(Collectors.toList());
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
		SpawnEggItem item = Utils.getEgg(type);
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
