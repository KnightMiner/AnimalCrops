package knightminer.animalcrops;

import knightminer.animalcrops.client.ClientEvents;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnimalCrops.modID)
public class AnimalCrops {
	public static final String modID = "animalcrops";
	public static final Logger log = LogManager.getLogger(modID);
	public static final IOptionalNamedTag<Block> CROP_SOIL = BlockTags.createOptional(Registration.getResource("crops_soil"));
	public static final IOptionalNamedTag<Block> SHROOM_SOIL = BlockTags.createOptional(Registration.getResource("shroom_soil"));

	public AnimalCrops() {
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->ClientEvents::new);
		Utils.initReflection();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
		MinecraftForge.EVENT_BUS.addListener(Registration::injectLoot);
		MinecraftForge.EVENT_BUS.addGenericListener(Block.class, Registration::missingBlockMappings);
		MinecraftForge.EVENT_BUS.addGenericListener(Item.class, Registration::missingItemMappings);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::configChanged);
	}
}
