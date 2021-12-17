package knightminer.animalcrops.plugins.jei;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Subtype interpretable registration mostly */
@JeiPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(AnimalCrops.modID, "jei");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		IIngredientSubtypeInterpreter<ItemStack> interpreter = (stack, context) -> Utils.getEntityID(stack.getTag()).orElse("");
		registry.registerSubtypeInterpreter(Registration.seeds, interpreter);
		registry.registerSubtypeInterpreter(Registration.anemonemalSeeds, interpreter);
		registry.registerSubtypeInterpreter(Registration.spores, interpreter);
		registry.registerSubtypeInterpreter(Registration.magnemoneSpores, interpreter);
	}
}
