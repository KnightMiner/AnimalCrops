package knightminer.animalcrops.core;

import com.google.common.collect.ImmutableList;
import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.items.AnimalPollenItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Config {

	private static final Builder BUILDER;
	public static final ForgeConfigSpec SPEC;

	// crop lists
	public static final AnimalCropType animalCrops;
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
	public static final AnimalCropType anemonemals;
	private static final List<String> ANIMAL_ANEMONEMAL_DEFAULTS = ImmutableList.of(
			"minecraft:cod",
			"minecraft:dolphin",
			"minecraft:pufferfish",
			"minecraft:salmon",
			"minecraft:squid",
			"minecraft:tropical_fish",
			"minecraft:turtle");
	public static final AnimalCropType animalShrooms;
	private static final List<String> ANIMAL_SHROOM_DEFAULTS = ImmutableList.of(
			"minecraft:hoglin",
			"minecraft:piglin");
	public static final AnimalCropType magnemones;
	private static final List<String> ANIMAL_MAGNEMONE_DEFAULTS = ImmutableList.of("minecraft:strider");
	public static final List<AnimalCropType> allCropTypes;

	public static final BooleanValue canBonemeal;

	// pollen
	public static final EnumValue<AnimalPollenItem.Action> pollenAction;
	public static final ConfigValue<List<? extends String>> pollenBlacklist;

	// grass drops
	public static final BooleanValue dropAnimalPollen;
	private static final ConfigValue<List<? extends String>> dropBlacklist;


	static {
		BUILDER = new Builder();

		// crop
		BUILDER.push("crop");
			canBonemeal = BUILDER
					.comment("Determines if bonemeal can be applied to the animal crops, anemonemals, animal shrooms, and magnemones")
					.define("bonemeal", false);
		ConfigValue<List<? extends String>>	animalCropsList = BUILDER
					.comment("List of animals to add as animal seeds. Must extend MobEntity and have a spawn egg.")
					.defineList("animalCrops", validateDefaults(ANIMAL_CROP_DEFAULTS), Config::validateAnimal);
		ConfigValue<List<? extends String>>	anemonemalsList = BUILDER
					.comment("List of water animals to add as water animal crops: anemonemals. Must extend MobEntity and have a spawn egg.")
					.defineList("anemonemals", validateDefaults(ANIMAL_ANEMONEMAL_DEFAULTS), Config::validateAnimal);
		ConfigValue<List<? extends String>>	animalShroomsList = BUILDER
					.comment("List of nether animals to add as nether animal shrooms. Must extend MobEntity and have a spawn egg.")
					.defineList("shrooms", validateDefaults(ANIMAL_SHROOM_DEFAULTS), Config::validateAnimal);
		ConfigValue<List<? extends String>>	magnemonesList = BUILDER
					.comment("List of lava animals to add as lava animal crops: magnemones. Must extend MobEntity and have a spawn egg.")
					.defineList("magnemones", validateDefaults(ANIMAL_MAGNEMONE_DEFAULTS), Config::validateAnimal);
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
		dropAnimalPollen = BUILDER
				.comment("If true, grass will rarely drop animal pollen")
				.define("animal_pollen", true);
		dropBlacklist = BUILDER
				.comment("Animals that will not drop from grass or sea grass, based on the other two lists")
				.defineList("blacklist", ImmutableList.of(), Config::validateAnimal);

		BooleanValue dropAnimalSeeds = BUILDER
					.comment("If true, grass will rarely drop a random animal seed")
					.define("animal_seeds", false);
		BooleanValue dropAnemonemals = BUILDER
					.comment("If true, sea grass will rarely drop a random anemonemal")
					.define("anemonemal", false);
		BooleanValue dropAnimalShrooms = BUILDER
				.comment("If true, nether sprouts will rarely drop a random animal shroom")
				.define("animal_shrooms", false);
		BooleanValue dropMagnemones = BUILDER
				.comment("If true, nether sprouts will rarely drop a random magnemones")
				.define("magnemones", false);
		BUILDER.pop();

		animalCrops = new AnimalCropType(animalCropsList, dropAnimalSeeds);
		anemonemals = new AnimalCropType(anemonemalsList, dropAnemonemals);
		animalShrooms = new AnimalCropType(animalShroomsList, dropAnimalShrooms);
		magnemones = new AnimalCropType(magnemonesList, dropMagnemones);
		ImmutableList.Builder<AnimalCropType> builder = ImmutableList.builder();
		builder.add(animalCrops, anemonemals, animalShrooms, magnemones);
		allCropTypes = builder.build();

		SPEC = BUILDER.build();
	}

	// registered in AnimalCrops
	public static void configChanged(final ModConfigEvent.Reloading event) {
		if (event.getConfig().getType() == ModConfig.Type.SERVER) {
			allCropTypes.forEach(AnimalCropType::clearCache);
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
		if (!(obj instanceof String animal)) {
			return false;
		}

		// must be a valid entity
		EntityType<?> type = EntityType.byString(animal).orElse(null);
		if (type == null) {
			AnimalCrops.log.error("Invalid entity {}, cannot find entity", animal);
			return false;
		}

		// must have a spawn egg, use that for colors
		SpawnEggItem item = ForgeSpawnEggItem.fromEntityType(type);
		if(item == null) {
			AnimalCrops.log.error("Invalid entity {}, must have a spawn egg", animal);
			return false;
		}

		return true;
	}

	/** Config setup for each type of animal crops */
	public static class AnimalCropType implements Supplier<List<? extends String>> {
		private final ConfigValue<List<? extends String>> types;
		private List<? extends String> randomDrops;
		private final BooleanValue drop;

		protected AnimalCropType(ConfigValue<List<? extends String>> types, BooleanValue drop) {
			this.types = types;
			this.drop = drop;
		}

		@Override
		public List<? extends String> get() {
			return types.get();
		}

		/**
		 * Returns true if this type of animal crops drops
		 */
		public boolean doesDrop() {
			return drop.get() && !getRandomDrops().isEmpty();
		}

		/**
		 * Gets the list of drops for random crop drops
		 * @return  List for random crops
		 */
		public List<? extends String> getRandomDrops() {
			if (randomDrops == null) {
				randomDrops = handleDropBlacklist(types);
			}
			return randomDrops;
		}

		/**
		 * Clears the cache of drop types
		 */
		public void clearCache() {
			randomDrops = null;
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
}
