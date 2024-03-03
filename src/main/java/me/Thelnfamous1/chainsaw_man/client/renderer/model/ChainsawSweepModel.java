package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawSweep;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;

import java.util.HashMap;
import java.util.Map;

public class ChainsawSweepModel extends CMProjectileModel<ChainsawSweep> {

    public static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(ChainsawManMod.MODID, "animations/chainsaw_sweep.animation.json");
    public static final ResourceLocation MODEL_LOCATION = new ResourceLocation(ChainsawManMod.MODID, "geo/chainsaw_sweep.geo.json");
    public static final Map<Integer, ResourceLocation> TEXTURE_LOCATIONS = new HashMap<>();
    private static final String[] BONE_NAMES = {"sweep", "sweep2"};

    @Override
    public ResourceLocation getAnimationFileLocation(ChainsawSweep animatable) {
        return ANIMATION_LOCATION;
    }

    @Override
    public ResourceLocation getModelLocation(ChainsawSweep object) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureLocation(ChainsawSweep object) {
        int textureId = object.getTexture();
        return TEXTURE_LOCATIONS.computeIfAbsent(textureId, id -> new ResourceLocation(ChainsawManMod.MODID, String.format("textures/particle/chainsaw_sweep_%d.png", id)));
    }

    @Override
    protected String[] getBoneNames() {
        return BONE_NAMES;
    }

    @Override
    public void setCustomAnimations(ChainsawSweep animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        this.updatePartVisibility(animatable);
    }

    public void updatePartVisibility(ChainsawSweep entity){
        IBone leftSweep = this.getBone("sweep2");
        if(leftSweep != null){
            boolean leftHidden = entity.isLeftHidden();
            if(leftSweep.isHidden() != leftHidden){
                if(!FMLEnvironment.production) ChainsawManMod.LOGGER.info("Setting left sweep hidden to {}", leftHidden);
            }
            leftSweep.setHidden(leftHidden);
        }
        IBone rightSweep = this.getBone("sweep");
        if(rightSweep != null){
            boolean rightHidden = entity.isRightHidden();
            if(rightSweep.isHidden() != rightHidden){
                if(!FMLEnvironment.production) ChainsawManMod.LOGGER.info("Setting right sweep hidden to {}", rightHidden);
            }
            rightSweep.setHidden(rightHidden);
        }
    }
}