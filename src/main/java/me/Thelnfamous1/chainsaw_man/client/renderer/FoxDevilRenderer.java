package me.Thelnfamous1.chainsaw_man.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.Thelnfamous1.chainsaw_man.client.renderer.model.FoxDevilModel;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class FoxDevilRenderer extends CMProjectileRenderer<FoxDevil> {
	public FoxDevilRenderer(EntityRendererManager renderManager) {
		super(renderManager, new FoxDevilModel());
	}

	@Override
	public RenderType getRenderType(FoxDevil animatable, float partialTicks, MatrixStack stack,
									IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
									ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(textureLocation);
	}
}