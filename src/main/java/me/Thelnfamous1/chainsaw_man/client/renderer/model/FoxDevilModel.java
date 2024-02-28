package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FoxDevilModel extends AnimatedGeoModel<FoxDevil> {

    @Override
    public ResourceLocation getAnimationFileLocation(FoxDevil entity) {
        return new ResourceLocation(ChainsawManMod.MODID, "animations/fox_devil.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(FoxDevil entity) {
        return new ResourceLocation(ChainsawManMod.MODID, "geo/fox_devil.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(FoxDevil entity) {
        return new ResourceLocation(ChainsawManMod.MODID, "textures/entity/fox_devil.png");
    }

    @Override
    public void setCustomAnimations(FoxDevil animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        float yRot = MathHelper.rotLerp(animationEvent.getPartialTick(), animatable.yRotO, animatable.yRot);
        float xRot = MathHelper.lerp(animationEvent.getPartialTick(), animatable.xRotO, animatable.xRot);
        this.rotateFox(yRot, xRot);
    }

    private void rotateFox(float yRot, float xRot) {
        IBone root = getAnimationProcessor().getBone("root");
        if(root != null){
            root.setRotationY(-yRot * CMUtil.DEG_TO_RAD);
            root.setRotationX(-xRot * CMUtil.DEG_TO_RAD);
        }
    }
}
