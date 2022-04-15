package knightminer.animalcrops.core;

import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

/** Class holding all tags used by the mod */
public class AnimalTags {
	/** Static initialization */
	public static void init() {}

	// blocks

	/** Blocks valid for planting animal crops */
	public static final TagKey<Block> CROP_SOIL = blockTag("crops_soil");
	/** Blocks valid for planting animal spores */
	public static final TagKey<Block> SHROOM_SOIL = blockTag("shroom_soil");

	// entities

	/** Entities that are able to be planted */
	public static final TagKey<EntityType<?>> PLANTABLE = entityTag("plantable");
	/** Entities for the overworld crop type */
	public static final TagKey<EntityType<?>> ANIMAL_CROPS = entityTag("plantable/animal_crops");
	/** Entities for the underwater crop type */
	public static final TagKey<EntityType<?>> ANEMONEMAL = entityTag("plantable/anemonemals");
	/** Entities for the nether crop type */
	public static final TagKey<EntityType<?>> ANIMAL_SHROOMS = entityTag("plantable/animal_shrooms");
	/** Entities for the under lava crop type */
	public static final TagKey<EntityType<?>> MAGNEMONES = entityTag("plantable/magnemones");
	/** Animal crop entities available as random drops */
	public static final TagKey<EntityType<?>> DROPPABLE_ANIMAL_CROPS = entityTag("droppable/animal_crops");
	/** Anemonemal entities available as random drops */
	public static final TagKey<EntityType<?>> DROPPABLE_ANEMONEMAL = entityTag("droppable/anemonemals");
	/** Animal shroom entities available as random drops */
	public static final TagKey<EntityType<?>> DROPPABLE_ANIMAL_SHROOMS = entityTag("droppable/animal_shrooms");
	/** Magnemone entities available as random drops */
	public static final TagKey<EntityType<?>> DROPPABLE_MAGNEMONES = entityTag("droppable/magnemones");
	/** Entities that pollen works on */
	public static final TagKey<EntityType<?>> POLLEN_REACTIVE = entityTag("pollen_reactive");

	/** Creates a tag for a block */
	private static TagKey<Block> blockTag(String name) {
		return BlockTags.create(Registration.getResource(name));
	}

	/** Creates a tag for a entity type */
	private static TagKey<EntityType<?>> entityTag(String name) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Registration.getResource(name));
	}
}
