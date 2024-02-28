package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import net.minecraft.util.ResourceLocation;

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
    protected String[] getBoneNames() {
        return BONE_NAMES;
    }
}
