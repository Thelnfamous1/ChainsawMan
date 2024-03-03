package me.Thelnfamous1.chainsaw_man.common;

import me.Thelnfamous1.chainsaw_man.common.ability.CMMorph;
import me.ichun.mods.morph.api.morph.MorphVariant;
import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class CMMorphHelper {
    public static LivingEntity getActiveMorphEntity(PlayerEntity serverPlayer) {
        return MorphHandler.INSTANCE.getActiveMorphEntity(serverPlayer);
    }

    public static MorphVariant createVariant(LivingEntity entity) {
        return MorphHandler.INSTANCE.createVariant(entity);
    }

    public static boolean demorph(ServerPlayerEntity serverPlayer) {
        return MorphHandler.INSTANCE.demorph(serverPlayer);
    }

    public static boolean morphTo(ServerPlayerEntity serverPlayer, World world, CMMorph morph) {
        return MorphHandler.INSTANCE.morphTo(serverPlayer, morph.getMorphVariant(world));
    }

    public static boolean isCMMorphVariant(ServerPlayerEntity serverPlayer, World world, CMMorph morph) {
        return MorphHandler.INSTANCE.getMorphInfo(serverPlayer).isCurrentlyThisVariant(morph.getMorphVariant(world).thisVariant);
    }

    @Nullable
    public static UUID getUuidOfPlayerForMorph(LivingEntity living) {
        return MorphHandler.INSTANCE.getUuidOfPlayerForMorph(living);
    }

    public static EntityPredicate getMorphAttackPredicate(LivingEntity entity) {
        return (new EntityPredicate()).selector(target -> {
            UUID uuidOfPlayerForMorph = getUuidOfPlayerForMorph(entity);
            return uuidOfPlayerForMorph == null || !uuidOfPlayerForMorph.equals(target.getUUID());
        });
    }
}
