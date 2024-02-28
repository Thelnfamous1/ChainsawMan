package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.CMMorphHelper;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundSpecialAttackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;

public class AttackChainsawManHandler extends DynamicInputHandler {

    public static final AttackChainsawManHandler INSTANCE = new AttackChainsawManHandler();

    private AttackChainsawManHandler() {
    }

    @Override
    protected void onKeyFirstPress() {
        LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity(Minecraft.getInstance().player);
        if (activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()) {
            ChainsawMan chainsawMan = (ChainsawMan) activeMorphEntity;
            if (!chainsawMan.isAttackAnimationInProgress()) {
                ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundSpecialAttackPacket(CMSpecialAttack.DUAL_SWIPE, true));
            }
        }
    }
}