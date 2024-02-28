package me.Thelnfamous1.chainsaw_man.common.ability;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevilAttackType;
import me.Thelnfamous1.chainsaw_man.common.network.KeyBindAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Consumer;

public enum CMAbility {
    SUMMON_FOX_DEVIL(serverPlayer -> {
        if(!serverPlayer.level.getGameRules().getBoolean(ChainsawManMod.getRuleAkiFox())) return;
        LivingEntity activeEntity = CMUtil.getActiveEntity(serverPlayer);
        if(!activeEntity.level.isClientSide){
            Vector3d startPos = activeEntity.getEyePosition(1.0F);

            double x = startPos.x;
            double y = startPos.y;
            double z = startPos.z;

            Vector3d baseOffset = CMUtil.yRotatedZVector(activeEntity.getBbWidth() * 0.5F, activeEntity.getYHeadRot());
            Vector3d shootVec = activeEntity.getViewVector(1.0F).normalize().scale(10);
            Vector3d targetVec = startPos.add(baseOffset).add(shootVec);
            double targetX = targetVec.x;
            double targetY = targetVec.y;
            double targetZ = targetVec.z;

            double xDist = targetX - x;
            double yDist = targetY - y;
            double zDist = targetZ - z;
            FoxDevil foxDevil = new FoxDevil(activeEntity.level, serverPlayer, xDist, yDist, zDist);
            foxDevil.startAttack(FoxDevilAttackType.BITE);
            foxDevil.setPosRaw(x, y, z);
            activeEntity.level.addFreshEntity(foxDevil);
        }
    }, doNothing(), doNothing());

    private static Consumer<ServerPlayerEntity> doNothing() {
        return (serverPlayer) -> {
        };
    }

    private static double getHeadX(LivingEntity serverPlayer) {
        return serverPlayer.getX(0.5D) + 0.5D;
    }

    private static double getHeadY(LivingEntity serverPlayer) {
        return serverPlayer.getEyeY();
    }

    private static double getHeadZ(LivingEntity serverPlayer) {
        return serverPlayer.getX(0.5D) + 0.5D;
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