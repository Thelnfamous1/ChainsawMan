package me.Thelnfamous1.chainsaw_man.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.Thelnfamous1.chainsaw_man.client.renderer.model.ChainsawSweepModel;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawSweep;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ChainsawSweepRenderer extends CMProjectileRenderer<ChainsawSweep> {

    public static final Map<ResourceLocation, RenderType> RENDER_TYPES = new HashMap<>();

    public ChainsawSweepRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ChainsawSweepModel());
    }

    @Override
    public RenderType getRenderType(ChainsawSweep animatable, float partialTicks, MatrixStack stack,
                                    IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        return RENDER_TYPES.computeIfAbsent(textureLocation, RenderType::entityTranslucent);
    }
}