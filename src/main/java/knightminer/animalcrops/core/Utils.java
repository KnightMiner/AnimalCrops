package knightminer.animalcrops.core;

import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
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
  public static Optional<String> getEntityID(@Nullable CompoundNBT tags) {
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
   * Gets the SpawnEggItem for the given entity type.
   * Required as {@link SpawnEggItem::getEgg(EntityType<?>)} is clientside only.
   * @param type  Entity type for the egg
   * @return  Spawn egg for the entity type
   */
  @Nullable
  public static SpawnEggItem getEgg(EntityType<?> type) {
    return SpawnEggItem.EGGS.get(type);
  }

  /**
   * Fills a stack of containers, shrinking it by 1
   * @param player     Player to give the item to and for creative checks
   * @param container  Container stack, may contain more than 1
   * @param filled     Filled container stack
   * @return  Filled stack if 1 container, leftover container if more than 1, dropping the filled
   */
  public static ItemStack fillContainer(PlayerEntity player, ItemStack container, ItemStack filled) {
    container = container.copy();
    if (!player.isCreative()) {
      container.shrink(1);
      if (container.isEmpty()) {
        return filled;
      }
    }
    if (!player.inventory.addItemStackToInventory(filled)) {
      player.dropItem(filled, false);
    }
    return container;
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
