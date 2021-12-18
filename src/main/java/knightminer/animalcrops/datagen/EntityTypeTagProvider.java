package knightminer.animalcrops.datagen;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.AnimalTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {
	public EntityTypeTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, AnimalCrops.modID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags() {
		this.tag(AnimalTags.PLANTABLE).addTags(AnimalTags.ANIMAL_CROPS, AnimalTags.ANEMONEMAL, AnimalTags.ANIMAL_SHROOMS, AnimalTags.MAGNEMONES);

		// these tags directly contain things plantable but not droppable
		this.tag(AnimalTags.ANIMAL_CROPS).addTag(AnimalTags.DROPPABLE_ANIMAL_CROPS);
		this.tag(AnimalTags.ANEMONEMAL).addTag(AnimalTags.DROPPABLE_ANEMONEMAL);
		this.tag(AnimalTags.ANIMAL_SHROOMS).addTag(AnimalTags.DROPPABLE_ANIMAL_SHROOMS);
		this.tag(AnimalTags.MAGNEMONES).addTag(AnimalTags.DROPPABLE_MAGNEMONES);

		// overworld
		this.tag(AnimalTags.DROPPABLE_ANIMAL_CROPS).add(
				EntityType.CHICKEN, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.RABBIT, EntityType.SHEEP,
				EntityType.BEE, EntityType.CAT, EntityType.FOX, EntityType.OCELOT, EntityType.PARROT, EntityType.WOLF,
				EntityType.DONKEY, EntityType.HORSE, EntityType.MULE, EntityType.LLAMA,
				EntityType.PANDA, EntityType.POLAR_BEAR,
				EntityType.VILLAGER)
				.addOptional(new ResourceLocation("waddles:adelie_penguin"));
		this.tag(AnimalTags.DROPPABLE_ANEMONEMAL).add(
				EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH,
				EntityType.DOLPHIN, EntityType.SQUID, EntityType.TURTLE);
		// nether
		this.tag(AnimalTags.DROPPABLE_ANIMAL_SHROOMS).add(EntityType.HOGLIN, EntityType.PIGLIN);
		this.tag(AnimalTags.DROPPABLE_MAGNEMONES).add(EntityType.STRIDER);

		// allows limiting pollen to just some mobs
		this.tag(AnimalTags.POLLEN_REACTIVE).addTag(AnimalTags.PLANTABLE);
	}
}
