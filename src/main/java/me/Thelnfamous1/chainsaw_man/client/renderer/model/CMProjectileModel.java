package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class CMProjectileModel<T extends Entity & IAnimatable> extends AnimatedGeoModel<T> {
    @Override
    public void setCustomAnimations(T animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        float yRot = MathHelper.rotLerp(animationEvent.getPartialTick(), animatable.yRotO, animatable.yRot);
        float xRot = MathHelper.lerp(animationEvent.getPartialTick(), animatable.xRotO, animatable.xRot);
        this.rotateBones(yRot, xRot, getBoneNames());
    }

    protected abstract String[] getBoneNames();

    private void rotateBones(float yRot, float xRot, String... boneNames) {
        for (String boneName : boneNames) {
            IBone bone = getAnimationProcessor().getBone(boneName);
            if (bone != null) {
                bone.setRotationY(-yRot * CMUtil.DEG_TO_RAD);
                bone.setRotationX(-xRot * CMUtil.DEG_TO_RAD);
            }
        }
    }
}
