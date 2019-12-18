package knightminer.animalcrops.core;

import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public abstract class Utils {
	public static final String ENTITY_TAG = "entity";

	private Utils() {}

  /**
   * Gets the entity ID from a set of tags
   * @param tags  Tag compound, from either a TE or a stack
   * @return  Entity resource location
   */
  @Nullable
  public static Optional<String> getEntityID(CompoundNBT tags) {
    // no tags? skip
    if (tags == null) {
      return Optional.empty();
    }

    // no entity? also give up
    if (!tags.contains(ENTITY_TAG, 8)) {
      return Optional.empty();
    }

    return Optional.of(tags.getString(ENTITY_TAG));
  }

  /**
   * Sets the entity ID for a given NBT tag
   * @param stack   Stack to set NBT, will be modified
   * @param entity  Entity string
   * @return  Stack with NBT set
   */
  public static ItemStack setEntityId(ItemStack stack, @Nullable String entity) {
    if(entity == null) {
      return stack;
    }
    stack.getOrCreateTag().putString(ENTITY_TAG, entity);
    return stack;
  }

  /**
   * Sets up reflection logic
   */
  private static Method setSlimeSize;
  public static void initReflection() {
    try {
      setSlimeSize = ObfuscationReflectionHelper.findMethod(SlimeEntity.class, "func_70799_a", int.class, boolean.class);
    } catch(ObfuscationReflectionHelper.UnableToFindMethodException ex) {
      AnimalCrops.log.error("Exception finding EntitySlime::setSlimeSize", ex);
    }
  }

  /**
   * Sets a slime's size using {@link SlimeEntity::setSlimeSize(int, boolean)}
   * @param slime  Slime instance
   * @param size   Slime size to use
   */
  public static void setSlimeSize(SlimeEntity slime, int size) {
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
