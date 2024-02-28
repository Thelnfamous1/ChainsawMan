package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawSweep;
import net.minecraft.util.ResourceLocation;

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
}