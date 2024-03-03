package me.Thelnfamous1.chainsaw_man.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.Thelnfamous1.chainsaw_man.client.renderer.model.ChainsawSweepModel;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawSweep;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ChainsawSweepRenderer extends CMProjectileRenderer<ChainsawSweep> {

    public static final Map<ResourceLocation, RenderType> RENDER_TYPES = new HashMap<>();
    private final ChainsawSweepModel customModel;

    public ChainsawSweepRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ChainsawSweepModel());
        this.customModel = (ChainsawSweepModel)this.modelProvider;
    }

    @Override
    public boolean shouldRender(ChainsawSweep p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        return super.shouldRender(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
    }

    @Override
    public void renderEarly(ChainsawSweep animatable, MatrixStack stackIn, float partialTicks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        //this.customModel.updatePartVisibility(animatable);
    }

    @Override
    public RenderType getRenderType(ChainsawSweep animatable, float partialTicks, MatrixStack stack,
                                    IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        return RENDER_TYPES.computeIfAbsent(textureLocation, RenderType::entityTranslucent);
    }
}