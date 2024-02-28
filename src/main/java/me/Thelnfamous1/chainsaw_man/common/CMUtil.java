package me.Thelnfamous1.chainsaw_man.common;

import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class CMUtil {

    public static final float DEG_TO_RAD = (float) (Math.PI / 180F);

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

    public static LivingEntity getActiveEntity(ServerPlayerEntity serverPlayer) {
        LivingEntity activeMorphEntity = MorphHandler.INSTANCE.getActiveMorphEntity(serverPlayer);
        return activeMorphEntity != null ? activeMorphEntity : serverPlayer;
    }

    public static void rotateTowardsMovement(Entity pProjectile, float pRotationSpeed, Vector3d movement) {
        if (movement.lengthSqr() != 0.0D) {
            float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(movement));
            pProjectile.yRot = (float)(MathHelper.atan2(movement.z, movement.x) * (double)(180F / (float)Math.PI)) + 90.0F;

            for(pProjectile.xRot = (float)(MathHelper.atan2(f, movement.y) * (double)(180F / (float)Math.PI)) - 90.0F; pProjectile.xRot - pProjectile.xRotO < -180.0F; pProjectile.xRotO -= 360.0F) {
            }

            while(pProjectile.xRot - pProjectile.xRotO >= 180.0F) {
                pProjectile.xRotO += 360.0F;
            }

            while(pProjectile.yRot - pProjectile.yRotO < -180.0F) {
                pProjectile.yRotO -= 360.0F;
            }

            while(pProjectile.yRot - pProjectile.yRotO >= 180.0F) {
                pProjectile.yRotO += 360.0F;
            }

            pProjectile.xRot = MathHelper.lerp(pRotationSpeed, pProjectile.xRotO, pProjectile.xRot);
            pProjectile.yRot = MathHelper.lerp(pRotationSpeed, pProjectile.yRotO, pProjectile.yRot);
        }
    }
}