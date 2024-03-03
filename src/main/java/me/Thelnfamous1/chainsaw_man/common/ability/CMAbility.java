package me.Thelnfamous1.chainsaw_man.common.ability;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevilAttackType;
import me.Thelnfamous1.chainsaw_man.common.network.KeyBindAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public enum CMAbility {
    SUMMON_FOX_DEVIL(serverPlayer -> {
        if(!serverPlayer.level.getGameRules().getBoolean(ChainsawManMod.getRuleAkiFox())) return;
        LivingEntity activeEntity = CMUtil.getActiveEntity(serverPlayer);
        if(!activeEntity.level.isClientSide){
            float verticalDist = ChainsawManMod.FOX_DEVIL.get().getHeight();
            float horizontalDist = ChainsawManMod.FOX_DEVIL.get().getWidth() * 2;
            Vector3d startPos = activeEntity.position().subtract(0, verticalDist, 0);

            double x = startPos.x;
            double y = startPos.y;
            double z = startPos.z;
            float xRot = (float) -Math.atan2(verticalDist, horizontalDist) * CMUtil.RAD_TO_DEG;
            if(!FMLEnvironment.production){
                ChainsawManMod.LOGGER.info("Trajectory xRot is {}", xRot);
            }
            double hypot = Math.hypot(horizontalDist, verticalDist);
            Vector3d shootVec = CMUtil.calculateViewVector(xRot, activeEntity.getViewYRot(1.0F))
                    .normalize()
                    .scale(hypot);
            if(!FMLEnvironment.production){
                ChainsawManMod.LOGGER.info("Shoot vector has length {}. Vertical length of {}, horizontal length of {}", shootVec.length(), shootVec.multiply(0, 1, 0).length(), shootVec.multiply(1, 0, 1).length());
            }
            Vector3d targetVec = startPos.add(shootVec);
            double targetX = targetVec.x;
            double targetY = targetVec.y;
            double targetZ = targetVec.z;

            double xDist = targetX - x;
            double yDist = targetY - y;
            double zDist = targetZ - z;
            FoxDevil foxDevil = new FoxDevil(activeEntity.level, serverPlayer, xDist, yDist, zDist);
            foxDevil.setAttackDelay(10);
            foxDevil.scaleStep(hypot / (foxDevil.getAttackDelay() + FoxDevilAttackType.BITE.getAttackDuration())); // hypot / (10 + 30 = 40 ticks total)
            if(!FMLEnvironment.production){
                ChainsawManMod.LOGGER.info("Spawned FoxDevil with step ({}, {}, {})", foxDevil.xStep, foxDevil.yStep, foxDevil.zStep);
            }
            //foxDevil.startAttack(FoxDevilAttackType.BITE);
            foxDevil.setPosRaw(x, y, z);
            activeEntity.level.addFreshEntity(foxDevil);
        }
    }, doNothing(), doNothing());

    private static Consumer<ServerPlayerEntity> doNothing() {
        return (serverPlayer) -> {
        };
    }

    private final Consumer<ServerPlayerEntity> onInitialPress;
    private final Consumer<ServerPlayerEntity> onHeld;
    private final Consumer<ServerPlayerEntity> onRelease;

    CMAbility(Consumer<ServerPlayerEntity> onInitialPress, Consumer<ServerPlayerEntity> onHeld, Consumer<ServerPlayerEntity> onRelease) {
        this.onInitialPress = onInitialPress;
        this.onHeld = onHeld;
        this.onRelease = onRelease;
    }

    public Consumer<ServerPlayerEntity> getHandlerForKeyBindAction(KeyBindAction keyBindAction) {
        switch (keyBindAction){
            case INITIAL_PRESS:
                return this.onInitialPress;
            case HELD:
                return this.onHeld;
            case RELEASE:
                return this.onRelease;
            default:
                return doNothing();
        }
    }

}