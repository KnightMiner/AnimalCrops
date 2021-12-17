package knightminer.animalcrops.items;

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
    String id = Objects.requireNonNull(type.getRegistryName()).toString();
    // check blacklist first, easiest
    if (!Config.pollenBlacklist.get().contains(id)) {
      // next, check which type of entity we are grabbing
      boolean isCrops = Config.animalCrops.get().contains(id);
      if (isCrops || Config.anemonemals.get().contains(id)) {
        // create the seed item
        ItemStack seeds = new ItemStack(isCrops ? Registration.seeds : Registration.anemonemalSeeds);
        Utils.setEntityId(seeds, id);
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
