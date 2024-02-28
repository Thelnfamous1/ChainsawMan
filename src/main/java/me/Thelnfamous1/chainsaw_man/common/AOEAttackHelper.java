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

    public static AxisAlignedBB createAttackBox(Entity entity, Vector3d attackRadii, float xRot, float yRot) {
        Vector3d baseOffset = CMUtil.xYRotatedZVector(entity.getBbWidth() * 0.5F, xRot, yRot);
        Vector3d attackOffset = CMUtil.xYRotatedZVector(attackRadii.z(), xRot, yRot);
        return AxisAlignedBB.ofSize(attackRadii.x() * 2, attackRadii.y() * 2, attackRadii.z() * 2)
                .move(entity.position()
                        .add(0, attackRadii.y(), 0)
                        .add(baseOffset)
                        .add(attackOffset));
    }

    public static void sendHitboxParticles(AxisAlignedBB attackBox, World level) {
        if(level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;
            RedstoneParticleData particle = new RedstoneParticleData(WHITE.x(), WHITE.y(), WHITE.z(), 2.0F);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
        }
    }
}
