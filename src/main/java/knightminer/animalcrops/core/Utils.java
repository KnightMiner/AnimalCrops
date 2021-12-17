package knightminer.animalcrops.core;

import knightminer.animalcrops.AnimalCrops;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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
  public static Optional<String> getEntityID(@Nullable CompoundTag tags) {
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
   * Fills a stack of containers, shrinking it by 1
   * @param player     Player to give the item to and for creative checks
   * @param container  Container stack, may contain more than 1
   * @param filled     Filled container stack
   * @return  Filled stack if 1 container, leftover container if more than 1, dropping the filled
   */
  public static ItemStack fillContainer(Player player, ItemStack container, ItemStack filled) {
    container = container.copy();
    if (!player.isCreative()) {
      container.shrink(1);
      if (container.isEmpty()) {
        return filled;
      }
    }
    if (!player.getInventory().add(filled)) {
      player.drop(filled, false);
    }
    return container;
  }

  /**
   * Sets up reflection logic
   */
  private static Method setSlimeSize;
  public static void initReflection() {
    try {
      setSlimeSize = ObfuscationReflectionHelper.findMethod(Slime.class, "m_7839_", int.class, boolean.class);
    } catch(ObfuscationReflectionHelper.UnableToFindMethodException ex) {
      AnimalCrops.log.error("Exception finding EntitySlime::setSlimeSize", ex);
    }
  }

  /**
   * Sets a slime's size using {@link Slime::setSize(int, boolean)}
   * @param slime  Slime instance
   * @param size   Slime size to use
   */
  public static void setSlimeSize(Slime slime, int size) {
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
