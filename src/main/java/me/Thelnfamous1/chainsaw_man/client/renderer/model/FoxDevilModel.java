package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;

public class FoxDevilModel extends CMProjectileModel<FoxDevil> {

    private static final String[] BONE_NAMES = {"root"};

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
        IBone root = this.getBone("root");
        if(root != null){
            root.setHidden(!animatable.isAttacking() && animatable.getLife() <= 0);
        }
    }

    @Override
    protected String[] getBoneNames() {
        return BONE_NAMES;
    }
}
