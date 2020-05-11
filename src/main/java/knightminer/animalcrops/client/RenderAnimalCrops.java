package knightminer.animalcrops.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import knightminer.animalcrops.tileentity.AnimalCropsTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.MobEntity;

public class RenderAnimalCrops extends TileEntityRenderer<AnimalCropsTileEntity> {
  private static final Minecraft mc = Minecraft.getInstance();

  public RenderAnimalCrops(TileEntityRendererDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(AnimalCropsTileEntity te, float delta, MatrixStack stack, IRenderTypeBuffer buffer, int lighting, int var6) {
    // check with the settings file to determine if this block renders its TE
    if(!Settings.shouldRenderEntity(te.getBlockState().getBlock())) {
      return;
    }
    int age = te.getBlockState().get(CropsBlock.AGE);
    if(age == 0) {
      return;
    }

    MobEntity entity = te.getEntity(true);
    if(entity == null) {
      return;
    }

    // its pretty easy, just draw the entity
    stack.push();
    stack.translate(0.5, 0, 0.5);
    // TODO: tint entity green, is this still possible?
    //RenderSystem.color3f(0.65f, 1.0f, 0.65f);
    //GlStateManager.color4f(0.65f, 1.0f, 0.65f, 1.0f);
    if(age < 7) {
      float scale = age / 7f;
      stack.scale(scale, scale, scale);
    }
    // renderEntityStatic(entity, x, y, z, rotation, delta, stack, buffer, lighting)
    mc.getRenderManager().renderEntityStatic(entity, 0, 0, 0, 0, 0, stack, buffer, lighting);
    //GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    //RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    stack.pop(); // pop matrix
  }
}
