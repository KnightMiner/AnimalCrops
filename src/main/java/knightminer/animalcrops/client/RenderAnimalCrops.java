package knightminer.animalcrops.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.MobEntity;

public class RenderAnimalCrops extends TileEntityRenderer<TileAnimalCrops> {
  private static final Minecraft mc = Minecraft.getInstance();

  @Override
  public void render(TileAnimalCrops te, double x, double y, double z, float partialTicks, int destroyStage) {
    int age = te.getBlockState().get(CropsBlock.AGE);
    if(age == 0) {
      return;
    }

    MobEntity entity = te.getEntity(true);
    if(entity == null) {
      return;
    }

    // its pretty easy, just draw the entity
    GlStateManager.pushMatrix();
    int brightness = mc.world.getCombinedLight(te.getPos(), 0);
    int j = brightness & 0xFFFF;
    int k = brightness >> 0x10 & 0xFFFF;
    //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
    GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, j, k);
    GlStateManager.color4f(0.65f, 1.0f, 0.65f, 1.0f);
    GlStateManager.translatef((float)x + 0.5f, (float)y, (float)z + 0.5f);
    if(age < 7) {
      float scale = age / 7f;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(scale, scale, scale);
    }
    mc.getRenderManager().renderEntity(entity, 0, 0, 0, entity.rotationYaw, 0, false);
    GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    GlStateManager.popMatrix();
  }
}
