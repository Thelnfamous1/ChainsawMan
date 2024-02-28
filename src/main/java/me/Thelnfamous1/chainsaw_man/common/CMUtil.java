package me.Thelnfamous1.chainsaw_man.common;

import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class CMUtil {

    public static final float DEG_TO_RAD = (float) (Math.PI / 180F);
    public static final float ATTACK_VOLUME = 4.0F;

    public static int secondsToTicks(float seconds){
        return MathHelper.ceil(seconds * 20);
    }

    public static Vector3d yRotatedXZVector(double x, double z, float yRot) {
        return new Vector3d(x, 0.0D, z).yRot(-yRot * DEG_TO_RAD);
    }

    public static Vector3d xYRotatedXZVector(double x, double z, float xRot, float yRot) {
        return new Vector3d(x, 0.0D, z).xRot(-xRot * DEG_TO_RAD).yRot(-yRot * DEG_TO_RAD);
    }

    public static Vector3d xYRotatedZVector(double z, float xRot, float yRot) {
        return xYRotatedXZVector(0, z, xRot, yRot);
    }

    public static Vector3d yRotatedZVector(double z, float yRot) {
        return yRotatedXZVector(0, z, yRot);
    }

    public static DamageSource getMorphDamageSource(LivingEntity living) {
        UUID uuidOfPlayerForMorph = MorphHandler.INSTANCE.getUuidOfPlayerForMorph(living);
        DamageSource pSource;
        if(uuidOfPlayerForMorph != null){
            PlayerEntity player = living.level.getPlayerByUUID(uuidOfPlayerForMorph);
            pSource = DamageSource.playerAttack(player);
        } else{
            pSource = DamageSource.mobAttack(living);
        }
        return pSource;
    }

    public static void spawnVanillaExplosionParticles(ServerWorld level, double radius, Vector3d posVec) {
        if(radius > 2){
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, posVec.x, posVec.y, posVec.z, 0, 1.0D, 0.0D, 0.0D, 1);
        } else{
            level.sendParticles(ParticleTypes.EXPLOSION, posVec.x, posVec.y, posVec.z, 0, 1.0D, 0.0D, 0.0D, 1);
        }
    }

    public static void areaOfEffectAttack(double shiftScale, double inflateScale, LivingEntity attacker, float knockbackScale, float damageScale, BasicParticleType particleType, SoundEvent soundEvent, int count, float yShift){
        float damage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float knockback = (float) attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        double xShift = (-MathHelper.sin(attacker.yBodyRot * ((float)Math.PI / 180F))) * shiftScale;
        double zShift = MathHelper.cos(attacker.yBodyRot * ((float)Math.PI  / 180F)) * shiftScale;

        UUID uuidOfPlayerForMorph = MorphHandler.INSTANCE.getUuidOfPlayerForMorph(attacker);
        PlayerEntity player = null;
        if(uuidOfPlayerForMorph != null){
            player = attacker.level.getPlayerByUUID(uuidOfPlayerForMorph);
        }

        for(LivingEntity target : attacker.level.getEntitiesOfClass(LivingEntity.class, attacker.getBoundingBox().move(xShift, 0.0D, zShift).inflate(inflateScale, 0.25D, inflateScale))) {
            if (target != attacker && target != player && !attacker.isAlliedTo(target) && (!(target instanceof ArmorStandEntity) || !((ArmorStandEntity) target).isMarker())) {
                target.knockback(knockback * knockbackScale, MathHelper.sin(attacker.yRot * ((float)Math.PI / 180F)), -MathHelper.cos(attacker.yRot * ((float)Math.PI / 180F)));
                DamageSource pSource = getMorphDamageSource(attacker);
                target.hurt(pSource, damage * damageScale);
            }
        }
        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), soundEvent, attacker.getSoundSource(), ATTACK_VOLUME, 1.0F);
        areaOfEffectParticles(attacker, shiftScale, count, 0.0D, particleType, yShift);
    }

    private static void areaOfEffectParticles(LivingEntity entity, double shiftScale, int count, double speed, BasicParticleType particleType, double yShift) {
        double xShift = -MathHelper.sin(entity.yRot * ((float)Math.PI / 180F)) * shiftScale;
        double zShift = MathHelper.cos(entity.yRot * ((float)Math.PI / 180F)) * shiftScale;
        if (entity.level instanceof ServerWorld) {
            ((ServerWorld)entity.level).sendParticles(particleType,
                    entity.getX() + xShift,
                    entity.getY(0.5D) + yShift,
                    entity.getZ() + zShift,
                    count,
                    xShift,
                    0.0D,
                    zShift,
                    speed);
        }
    }

    public static LivingEntity getActiveEntity(ServerPlayerEntity serverPlayer) {
        LivingEntity activeMorphEntity = MorphHandler.INSTANCE.getActiveMorphEntity(serverPlayer);
        return activeMorphEntity != null ? activeMorphEntity : serverPlayer;
    }
}