package knightminer.animalcrops.datagen;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.AnimalTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class BlockTagProvider extends BlockTagsProvider {
	public BlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, AnimalCrops.modID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(AnimalTags.CROP_SOIL).add(Blocks.GRASS_BLOCK, Blocks.PODZOL);
		this.tag(AnimalTags.SHROOM_SOIL).add(Blocks.SOUL_SOIL, Blocks.SOUL_SAND);
	}
}
