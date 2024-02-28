package me.Thelnfamous1.chainsaw_man.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.Thelnfamous1.chainsaw_man.client.renderer.model.ChainsawManModel;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChainsawManRenderer extends GeoEntityRenderer<ChainsawMan> {
	public ChainsawManRenderer(EntityRendererManager renderManager) {
		super(renderManager, new ChainsawManModel());
	}

	@Override
	public RenderType getRenderType(ChainsawMan animatable, float partialTicks, MatrixStack stack,
                                    IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(textureLocation);
	}
}