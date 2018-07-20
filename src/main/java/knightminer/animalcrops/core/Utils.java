package knightminer.animalcrops.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public abstract class Utils {
	public static final String ENTITY_TAG = "entity";

	private Utils() {}

    public static ResourceLocation getEntityID(NBTTagCompound tags) {
        // no tags? skip
        if (tags == null) {
            return null;
        }

        // no entity? also give up
        if (!tags.hasKey(ENTITY_TAG, 8)) {
            return null;
        }

        String entityID = tags.getString(ENTITY_TAG);
        ResourceLocation loc = new ResourceLocation(entityID);
        if (!entityID.contains(":")) {
        	tags.setString(ENTITY_TAG, loc.toString());
        }

        return loc;
    }

    /**
     * Sets up reflection logic
     */
    private static Method setSlimeSize;
    public static void initReflection() {
    	try {
    		setSlimeSize = ReflectionHelper.findMethod(EntitySlime.class, "setSlimeSize", "func_70799_a", int.class, boolean.class);
    	} catch(UnableToFindMethodException ex) {
			AnimalCrops.log.error("Exception finding EntitySlime::setSlimeSize", ex);
    	}
    }

    /**
     * Sets a slime's size using {@link EntitySlime::setSlimeSize(int, boolean)}
     * @param slime  Slime instance
     * @param size   Slime size to use
     */
    public static void setSlimeSize(EntitySlime slime, int size) {
    	if(setSlimeSize == null) {
    		return;
    	}
		try {
			setSlimeSize.invoke(slime, size, true);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			AnimalCrops.log.error("Caught exception trying to set slime size", ex);
		}
    }
}
