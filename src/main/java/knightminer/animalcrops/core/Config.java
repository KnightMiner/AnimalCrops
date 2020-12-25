package knightminer.animalcrops.core;

import com.google.common.collect.ImmutableList;
import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.items.AnimalPollenItem;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Config {

	private static final Builder BUILDER;
	public static final ForgeConfigSpec SPEC;

	// crop
	public static ConfigValue<List<? extends String>> animalCrops;
	private static final List<String> ANIMAL_CROP_DEFAULTS = ImmutableList.of(
			"minecraft:bee",
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
	public static ConfigValue<List<? extends String>> anemonemals;
	private static final List<String> ANIMAL_ANEMONEMAL_DEFAULTS = ImmutableList.of(
			"minecraft:cod",
			"minecraft:dolphin",
			"minecraft:pufferfish",
			"minecraft:salmon",
			"minecraft:squid",
			"minecraft:tropical_fish",
			"minecraft:turtle");
	public static BooleanValue canBonemeal;

	// pollen
	public static EnumValue<AnimalPollenItem.Action> pollenAction;
	public static ConfigValue<List<? extends String>> pollenBlacklist;

	// grass drops
	public static BooleanValue dropAnimalPollen;
	private static final ConfigValue<List<? extends String>> dropBlacklist;
	// animal seeds
	private static List<? extends String> randomAnimalCrops;
	/** Returns the list of animal crops filtered by the drop blacklist */
	public static List<? extends String> randomAnimalCrops() {
		if (randomAnimalCrops != null) {
			return randomAnimalCrops;
		}
		return randomAnimalCrops = handleDropBlacklist(animalCrops);
	}
	private static final BooleanValue dropAnimalSeeds;
	public static boolean dropAnimalSeeds() {
		return dropAnimalSeeds.get() && !randomAnimalCrops().isEmpty();
	}
	// anemonemals
	private static List<? extends String> randomAnemonemals;
	/** Returns the list of anemonemals filtered by the drop blacklist */
	public static List<? extends String> randomAnemonemals() {
		if (randomAnemonemals != null) {
			return randomAnemonemals;
		}
		return randomAnemonemals = handleDropBlacklist(anemonemals);
	}
	private static final BooleanValue dropAnemonemals;
	public static boolean dropAnemonemals() {
		return dropAnemonemals.get() && !randomAnemonemals().isEmpty();
	}

	static {
		BUILDER = new Builder();

		// crop
		BUILDER.push("crop");
		{
			canBonemeal = BUILDER
					.comment("Determines if bonemeal can be applied to the animal crop")
					.define("bonemeal", false);
			animalCrops = BUILDER
					.comment("List of animals to add as animal seeds. Must extend MobEntity and have a spawn egg.")
					.defineList("animalCrops", validateDefaults(ANIMAL_CROP_DEFAULTS), Config::validateAnimal);
			anemonemals = BUILDER
					.comment("List of water animals to add as water animal crops: anemonemals. Must extend MobEntity and have a spawn egg.")
					.defineList("anemonemals", validateDefaults(ANIMAL_ANEMONEMAL_DEFAULTS), Config::validateAnimal);
		}
		BUILDER.pop();

		// spores
		Config.BUILDER.push("pollen");
		{
			pollenAction = BUILDER
					.comment("If CONSUME, the entity is killed when pollen are used, though no items are dropped",
					         "If DAMAGE, the entity will take 2 hearts of damage when pollen are used")
					.defineEnum("action", AnimalPollenItem.Action.DAMAGE);
			pollenBlacklist = BUILDER
					.comment("Animals that pollen cannot be used on, from either animal crops or anemonemals")
					.defineList("blacklist", ImmutableList.of(), Config::validateAnimal);
		}
		BUILDER.pop();

		// grass drops
		BUILDER.push("grassDrops");
		{
			dropAnimalSeeds = BUILDER
					.comment("If true, grass will rarely drop a random animal seed")
					.define("animal_seeds", false);
			dropAnemonemals = BUILDER
					.comment("If true, sea grass will rarely drop a random anemonemal")
					.define("anemonemal", false);
			dropAnimalPollen = BUILDER
					.comment("If true, grass will rarely drop animal pollen")
					.define("animal_pollen", true);
			dropBlacklist = BUILDER
					.comment("Animals that will not drop from grass or sea grass, based on the other two lists")
					.defineList("blacklist", ImmutableList.of(), Config::validateAnimal);
		}
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	// registered in AnimalCrops
	public static void configChanged(final ModConfig.Reloading event) {
		if (event.getConfig().getType() == ModConfig.Type.SERVER) {
			randomAnimalCrops = null;
			randomAnemonemals = null;
		}
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
	 * @param obj  Object that may be an entity ID
	 * @return  Validates the entity ID is available
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

		return true;
	}

	/**
	 * Filters the given property by the drop blacklist, returning the filtered list
	 * @param prop  Property to filter
	 * @return  The filtered list
	 */
	private static List<? extends String> handleDropBlacklist(ConfigValue<List<? extends String>> prop) {
		List<? extends String> list = prop.get();
		if (list.isEmpty()) {
			return ImmutableList.of();
		}
		List<? extends String> blacklist = dropBlacklist.get();
		if (blacklist.isEmpty()) {
			return list;
		}
		return list.stream()
		           .filter((id) -> !blacklist.contains(id))
		           .collect(Collectors.toList());
	}
}
