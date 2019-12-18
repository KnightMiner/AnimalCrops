package knightminer.animalcrops.plugins.jei;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(AnimalCrops.modID, "jei");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		ISubtypeInterpreter interpreter = (stack) -> Utils.getEntityID(stack.getTag()).orElse("");

		registry.registerSubtypeInterpreter(Registration.seeds, interpreter);
		registry.registerSubtypeInterpreter(Registration.lilySeeds, interpreter);
	}
}
