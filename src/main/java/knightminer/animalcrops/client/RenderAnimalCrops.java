package knightminer.animalcrops.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
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
  public void func_225616_a_(AnimalCropsTileEntity te, float delta, MatrixStack stack, IRenderTypeBuffer buffer, int lighting, int var6) {
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
    stack.func_227860_a_(); // push matrix
    stack.func_227861_a_(0.5, 0, 0.5); // translate
    // TODO: tint entity green, is this still possible?
    // GlStateManager.color4f(0.65f, 1.0f, 0.65f, 1.0f);
    if(age < 7) {
      float scale = age / 7f;
      stack.func_227862_a_(scale, scale, scale); // scale
    }
    // renderEntity(entity, x, y, z, rotation, delta, stack, buffer, lighting)
    mc.getRenderManager().func_229084_a_(entity, 0, 0, 0, 0, 0, stack, buffer, lighting);
    //GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    stack.func_227865_b_(); // pop matrix
  }
}
