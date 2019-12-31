package knightminer.animalcrops.items;

import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Registration;
import knightminer.animalcrops.core.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AnimalPollenItem extends Item {

  public AnimalPollenItem(Properties props) {
    super(props);
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    EntityType<?> type = entity.getType();
    String id = type.getRegistryName().toString();
    // check blacklist first, easiest
    if (!Config.pollenBlacklist.get().contains(id)) {
      // next, check which type of entity we are grabbing
      boolean isCrops = Config.animalCrops.get().contains(id);
      if (isCrops || Config.anemonemals.get().contains(id)) {
        // create the seed item
        ItemStack seeds = new ItemStack(isCrops ? Registration.seeds : Registration.anemonemalSeeds);
        Utils.setEntityId(seeds, id);
        player.setHeldItem(hand, Utils.fillContainer(player, stack, seeds));

        // effects
        entity.playSound(SoundEvents.BLOCK_NETHER_WART_BREAK, 1.0F, 0.8F);
        spawnEntityParticles(entity, ParticleTypes.MYCELIUM, 15);
        switch(Config.pollenAction.get()) {
          case CONSUME:
            // spawn death particles and remove the entity
            spawnEntityParticles(entity, ParticleTypes.POOF, 20);
            if (!entity.getEntityWorld().isRemote()) {
              entity.remove();
            }
            break;

          case DAMAGE:
            entity.attackEntityFrom(DamageSource.CACTUS, 4);
            spawnEntityParticles(entity, ParticleTypes.DAMAGE_INDICATOR, 2);
            break;
        }
        return true;
      }
    }

    // tell the player why nothing happened
    player.sendStatusMessage(new TranslationTextComponent(this.getTranslationKey() + ".invalid", type.getName()), true);
    return true;
  }

  /**
   * Spawns particles around an entity, relative to its size
   * @param entity    Entity
   * @param particle  Particle to spawn
   * @param count     Number to spawn
   */
  private static void spawnEntityParticles(LivingEntity entity, IParticleData particle, int count) {
    World world = entity.getEntityWorld();
    double width = entity.getWidth();
    double height = entity.getHeight();
    for(int k = 0; k < count; k++) {
      double speedX = world.rand.nextGaussian() * 0.02D;
      double speedY = world.rand.nextGaussian() * 0.02D;
      double speedZ = world.rand.nextGaussian() * 0.02D;
      world.addParticle(particle,
                        entity.posX + (world.rand.nextFloat() * width * 2.0F) - width,
                        entity.posY + (world.rand.nextFloat() * height),
                        entity.posZ + (world.rand.nextFloat() * width * 2.0F) - width,
                        speedX, speedY, speedZ);
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
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
