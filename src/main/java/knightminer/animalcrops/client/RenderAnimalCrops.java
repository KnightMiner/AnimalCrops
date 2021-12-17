package knightminer.animalcrops.client;

import com.mojang.blaze3d.vertex.PoseStack;
import knightminer.animalcrops.blocks.entity.AnimalCropsBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RenderAnimalCrops implements BlockEntityRenderer<AnimalCropsBlockEntity> {
  private static final Minecraft mc = Minecraft.getInstance();

  public RenderAnimalCrops(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(AnimalCropsBlockEntity be, float delta, PoseStack stack, MultiBufferSource buffer, int lighting, int var6) {
    // check with the settings file to determine if this block renders its TE
    BlockState state = be.getBlockState();
    if(!Settings.shouldRenderEntity(state.getBlock())) {
      return;
    }
    int age = state.getValue(CropBlock.AGE);
    if (age == 0) {
      return;
    }

    Mob entity = be.getEntity(true);
    if (entity == null) {
      return;
    }

    // its pretty easy, just draw the entity
    stack.pushPose();
    stack.translate(0.5, 0, 0.5);
    // TODO: tint entity green, is this still possible?
    //RenderSystem.color3f(0.65f, 1.0f, 0.65f);
    //GlStateManager.color4f(0.65f, 1.0f, 0.65f, 1.0f);
    if(age < 7) {
      float scale = age / 7f;
      stack.scale(scale, scale, scale);
    }
    // renderEntityStatic(entity, x, y, z, rotation, delta, stack, buffer, lighting)
    mc.getEntityRenderDispatcher().render(entity, 0, 0, 0, 0, 0, stack, buffer, lighting);
    //GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    //RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    stack.popPose();
  }
}
