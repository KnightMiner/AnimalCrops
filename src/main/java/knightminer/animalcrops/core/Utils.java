package knightminer.animalcrops.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

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
}
