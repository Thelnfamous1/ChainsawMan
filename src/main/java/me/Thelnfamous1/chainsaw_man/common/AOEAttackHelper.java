package me.Thelnfamous1.chainsaw_man.common;

import net.minecraft.entity.Entity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AOEAttackHelper {
    public static final Vector3f WHITE = new Vector3f(Vector3d.fromRGB24(16777215));
    public static final RedstoneParticleData HITBOX_CORNER_PARTICLE = new RedstoneParticleData(WHITE.x(), WHITE.y(), WHITE.z(), 2.0F);

    public static AxisAlignedBB createAttackBox(Entity entity, Vector3d attackRadii) {
        return AxisAlignedBB.ofSize(attackRadii.x() * 2, attackRadii.y() * 2, attackRadii.z() * 2)
                .move(entity.position()
                        .add(0, attackRadii.y(), 0));
    }

    public static AxisAlignedBB createAttackBox(Entity entity, Vector3d attackRadii, Vector3d movement) {
        Vector3d baseOffset = movement.normalize().scale(entity.getBbWidth() * 0.5F);
        Vector3d attackOffset = movement.normalize().scale(attackRadii.z());
        return createAttackBox(entity, attackRadii)
                .move(baseOffset
                        .add(attackOffset));
    }

    public static AxisAlignedBB createAttackBox(Entity entity, Vector3d attackRadii, float xRot, float yRot) {
        Vector3d baseOffset = CMUtil.xYRotatedZVector(entity.getBbWidth() * 0.5F, xRot, yRot);
        Vector3d attackOffset = CMUtil.xYRotatedZVector(attackRadii.z(), xRot, yRot);
        return createAttackBox(entity, attackRadii)
                        .move(baseOffset
                                .add(attackOffset));
    }

    public static void sendHitboxParticles(AxisAlignedBB attackBox, World level) {
        if(level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.minX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.maxX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.minX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.maxX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.minX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.maxX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.minX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(HITBOX_CORNER_PARTICLE, attackBox.maxX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
        }
    }
}
