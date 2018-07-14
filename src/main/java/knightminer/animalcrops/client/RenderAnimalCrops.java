package knightminer.animalcrops.client;

import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityCreature;

public class RenderAnimalCrops extends TileEntitySpecialRenderer<TileAnimalCrops> {
	private static final Minecraft mc = Minecraft.getMinecraft();
    @Override
	public void render(TileAnimalCrops te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    	int meta = te.getBlockMetadata();
    	if(meta == 0) {
    		return;
    	}
    	EntityCreature entity = te.getEntity();
    	if(entity == null) {
    		return;
    	}

    	// its pretty easy, just draw the entity
    	GlStateManager.pushMatrix();
		int brightness = mc.world.getCombinedLight(te.getPos(), 0);
        int j = brightness & 0xFFFF;
        int k = brightness >> 0x10 & 0xFFFF;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
        GlStateManager.color(0.75F, 1.0F, 0.75F, 1.0F);
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        if(meta < 7) {
            float scale = meta / 7f;
            GlStateManager.scale(scale, scale, scale);
        }
    	mc.getRenderManager().doRenderEntity(entity, 0, 0, 0, entity.rotationYaw, 0, false);
    	GlStateManager.popMatrix();
    }
}
