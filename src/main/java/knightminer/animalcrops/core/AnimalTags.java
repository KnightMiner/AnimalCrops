package knightminer.animalcrops.core;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

/** Class holding all tags used by the mod */
public class AnimalTags {
	/** Static initialization */
	public static void init() {}

	// blocks

	/** Blocks valid for planting animal crops */
	public static final IOptionalNamedTag<Block> CROP_SOIL = blockTag("crops_soil");
	/** Blocks valid for planting animal spores */
	public static final IOptionalNamedTag<Block> SHROOM_SOIL = blockTag("shroom_soil");

	// entities

	/** Entities that are able to be planted */
	public static final IOptionalNamedTag<EntityType<?>> PLANTABLE = entityTag("plantable");
	/** Entities for the overworld crop type */
	public static final IOptionalNamedTag<EntityType<?>> ANIMAL_CROPS = entityTag("plantable/animal_crops");
	/** Entities for the underwater crop type */
	public static final IOptionalNamedTag<EntityType<?>> ANEMONEMAL = entityTag("plantable/anemonemals");
	/** Entities for the nether crop type */
	public static final IOptionalNamedTag<EntityType<?>> ANIMAL_SHROOMS = entityTag("plantable/animal_shrooms");
	/** Entities for the under lava crop type */
	public static final IOptionalNamedTag<EntityType<?>> MAGNEMONES = entityTag("plantable/magnemones");
	/** Animal crop entities available as random drops */
	public static final IOptionalNamedTag<EntityType<?>> DROPPABLE_ANIMAL_CROPS = entityTag("droppable/animal_crops");
	/** Anemonemal entities available as random drops */
	public static final IOptionalNamedTag<EntityType<?>> DROPPABLE_ANEMONEMAL = entityTag("droppable/anemonemals");
	/** Animal shroom entities available as random drops */
	public static final IOptionalNamedTag<EntityType<?>> DROPPABLE_ANIMAL_SHROOMS = entityTag("droppable/animal_shrooms");
	/** Magnemone entities available as random drops */
	public static final IOptionalNamedTag<EntityType<?>> DROPPABLE_MAGNEMONES = entityTag("droppable/magnemones");
	/** Entities that pollen works on */
	public static final IOptionalNamedTag<EntityType<?>> POLLEN_REACTIVE = entityTag("pollen_reactive");

	/** Creates a tag for a block */
	private static IOptionalNamedTag<Block> blockTag(String name) {
		return BlockTags.createOptional(Registration.getResource(name));
	}

	/** Creates a tag for a entity type */
	private static IOptionalNamedTag<EntityType<?>> entityTag(String name) {
		return EntityTypeTags.createOptional(Registration.getResource(name));
	}
}
