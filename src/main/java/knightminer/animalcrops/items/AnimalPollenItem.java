package knightminer.animalcrops.items;

import knightminer.animalcrops.core.AnimalTags;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Item to convert mobs into seeds
 */
public class AnimalPollenItem extends Item {

  public AnimalPollenItem(Properties props) {
    super(props);
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
    EntityType<?> type = entity.getType();
    if (type.is(AnimalTags.POLLEN_REACTIVE)) {
      // next, check which type of seed we are grabbing
      Item item = null;
      if (type.is(AnimalTags.ANIMAL_CROPS)) {
        item = Registration.seeds;
      }
      else if (type.is(AnimalTags.ANEMONEMAL)) {
        item = Registration.anemonemalSeeds;
      }
      else if (type.is(AnimalTags.ANIMAL_SHROOMS)) {
        item = Registration.spores;
      }
      else if (type.is(AnimalTags.MAGNEMONES)) {
        item = Registration.magnemoneSpores;
      }
      // its possible the type matches none because someone used tags wrongly
      if (item != null) {
        // create the seed item
        ItemStack seeds = new ItemStack(item);
        Utils.setEntityId(seeds, Objects.requireNonNull(type.getRegistryName()).toString());
        player.setItemInHand(hand, Utils.fillContainer(player, stack, seeds));

        // effects
        entity.playSound(SoundEvents.NETHER_WART_BREAK, 1.0F, 0.8F);
        spawnEntityParticles(entity, ParticleTypes.MYCELIUM, 15);
        switch (Config.pollenAction.get()) {
          case CONSUME -> {
            // spawn death particles and remove the entity
            spawnEntityParticles(entity, ParticleTypes.POOF, 20);
            if (!entity.getLevel().isClientSide()) {
              entity.remove(RemovalReason.KILLED);
            }
          }
          case DAMAGE -> {
            entity.hurt(DamageSource.CACTUS, 4);
            spawnEntityParticles(entity, ParticleTypes.DAMAGE_INDICATOR, 2);
          }
        }
        return InteractionResult.SUCCESS;
      }
    }

    // tell the player why nothing happened
    player.displayClientMessage(new TranslatableComponent(this.getDescriptionId() + ".invalid", type.getDescription()), true);
    return InteractionResult.SUCCESS;
  }

  /**
   * Spawns particles around an entity, relative to its size
   * @param entity    Entity
   * @param particle  Particle to spawn
   * @param count     Number to spawn
   */
  private static void spawnEntityParticles(LivingEntity entity, ParticleOptions particle, int count) {
    Level world = entity.getLevel();
    for(int k = 0; k < count; k++) {
      double speedX = world.random.nextGaussian() * 0.02D;
      double speedY = world.random.nextGaussian() * 0.02D;
      double speedZ = world.random.nextGaussian() * 0.02D;
      world.addParticle(particle, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D), speedX, speedY, speedZ);
    }
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
    tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".tooltip"));
  }

  /** Valid actions for spores, as set by the config */
  public enum Action {
    /** Entity is consumed in a cloud of smoke */
    CONSUME,
    /** Entity takes damage, but remains in the world */
    DAMAGE,
    /** No action against entity */
    NONE
  }
}
