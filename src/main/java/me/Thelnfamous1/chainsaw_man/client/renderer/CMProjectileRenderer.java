package me.Thelnfamous1.chainsaw_man.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

import java.util.Collections;

public abstract class CMProjectileRenderer<T extends Entity & IAnimatable> extends GeoProjectilesRenderer<T> {
    public CMProjectileRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(entityIn));
        this.dispatchedMat = matrixStackIn.last().pose().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        matrixStackIn.pushPose();
        //matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90.0F));
        //matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)));
        float lastLimbDistance = 0.0F;
        float limbSwing = 0.0F;
        EntityModelData entityModelData = new EntityModelData();
        AnimationEvent<T> predicate = new AnimationEvent<>(entityIn, limbSwing, lastLimbDistance, partialTicks, !(lastLimbDistance > -0.15F) || !(lastLimbDistance < 0.15F), Collections.singletonList(entityModelData));
        if (this.modelProvider instanceof IAnimatableModel) {
            this.modelProvider.setCustomAnimations(entityIn, this.getUniqueID(entityIn), predicate);
        }

        Minecraft.getInstance().textureManager.bind(this.getTextureLocation(entityIn));
        Color renderColor = this.getRenderColor(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn);
        RenderType renderType = this.getRenderType(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, this.getTextureLocation(entityIn));
        this.render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, null, packedLightIn, getPackedOverlay(entityIn, 0.0F), (float) renderColor.getRed() / 255.0F, (float) renderColor.getGreen() / 255.0F, (float) renderColor.getBlue() / 255.0F, (float) renderColor.getAlpha() / 255.0F);
        matrixStackIn.popPose();

        // vanilla nameplate rendering from EntityRenderer#render
        RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(animatable, animatable.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        if (renderNameplateEvent.getResult() != Event.Result.DENY && (renderNameplateEvent.getResult() == Event.Result.ALLOW || this.shouldShowName(animatable))) {
            this.renderNameTag(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
