package me.Thelnfamous1.chainsaw_man.common.ability;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.ichun.mods.morph.api.morph.MorphVariant;
import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public enum CMMorph {
    CHAINSAW_MAN(ChainsawManMod.CHAINSAW_MAN.get());

    private final EntityType<? extends LivingEntity> type;
    private LivingEntity dummy;
    private MorphVariant morphVariant;

    CMMorph(EntityType<? extends LivingEntity> type){
        this.type = type;
    }

    public EntityType<? extends LivingEntity> getType() {
        return this.type;
    }

    public LivingEntity getOrCreateDummy(World world) {
        if(this.dummy == null){
            this.dummy = this.type.create(world);
        }
        return this.dummy;
    }

    public MorphVariant getMorphVariant(World world) {
        if(this.morphVariant == null){
            this.morphVariant = MorphHandler.INSTANCE.createVariant(this.getOrCreateDummy(world));
        }
        return this.morphVariant;
    }
}