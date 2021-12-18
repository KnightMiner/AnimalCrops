package knightminer.animalcrops.core;

import knightminer.animalcrops.items.AnimalPollenItem;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import javax.annotation.Nullable;
import java.util.Random;

public class Config {

	private static final Builder BUILDER;
	public static final ForgeConfigSpec SPEC;

	// types
	public static final AnimalCropType animalCrops;
	public static final AnimalCropType anemonemals;
	public static final AnimalCropType animalShrooms;
	public static final AnimalCropType magnemones;
	// genera
	public static final BooleanValue canBonemeal;
	public static final EnumValue<AnimalPollenItem.Action> pollenAction;
	// grass drops
	public static final BooleanValue dropAnimalPollen;

	static {
		BUILDER = new Builder();

		// crop
		BUILDER.push("general");
		{
			canBonemeal = BUILDER
					.comment("Determines if bonemeal can be applied to the animal crops, anemonemals, animal shrooms, and magnemones")
					.define("bonemeal", false);
			pollenAction = BUILDER
					.comment("If CONSUME, the entity is killed when pollen are used, though no items are dropped",
									 "If DAMAGE, the entity will take 2 hearts of damage when pollen are used")
					.defineEnum("action", AnimalPollenItem.Action.DAMAGE);
		}
		BUILDER.pop();

		// grass drops
		BUILDER.push("grassDrops");
		dropAnimalPollen = BUILDER
				.comment("If true, grass will rarely drop animal pollen")
				.define("animal_pollen", true);
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

		animalCrops   = new AnimalCropType(AnimalTags.DROPPABLE_ANIMAL_CROPS, dropAnimalSeeds);
		anemonemals   = new AnimalCropType(AnimalTags.DROPPABLE_ANEMONEMAL, dropAnemonemals);
		animalShrooms = new AnimalCropType(AnimalTags.DROPPABLE_ANIMAL_SHROOMS, dropAnimalShrooms);
		magnemones    = new AnimalCropType(AnimalTags.DROPPABLE_MAGNEMONES, dropMagnemones);

		SPEC = BUILDER.build();
	}

	// registered in AnimalCrops
	public static void configChanged(final ModConfigEvent.Reloading event) {}

	/** Config setup for each type of animal crops */
	public static class AnimalCropType {
		private final IOptionalNamedTag<EntityType<?>> tag;
		private final BooleanValue drop;

		protected AnimalCropType(IOptionalNamedTag<EntityType<?>> tag, BooleanValue drop) {
			this.tag = tag;
			this.drop = drop;
		}

		/**
		 * Returns true if this type of animal crops drops
		 */
		public boolean doesDrop() {
			return drop.get() && !tag.isDefaulted() && !tag.getValues().isEmpty();
		}

		/** Gets a random value of this crop drop type */
		@Nullable
		public EntityType<?> getRandomValue(Random random) {
			if (doesDrop()) {
				return tag.getRandomElement(random);
			}
			return null;
		}
	}
}
