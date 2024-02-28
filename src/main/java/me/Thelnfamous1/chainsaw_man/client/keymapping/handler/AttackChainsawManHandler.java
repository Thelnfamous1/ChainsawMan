package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundSpecialAttackPacket;

public class AttackChainsawManHandler extends DynamicInputHandler {

    public static final AttackChainsawManHandler INSTANCE = new AttackChainsawManHandler();

    private AttackChainsawManHandler() {
    }

    @Override
    protected void onKeyFirstPress() {
        ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundSpecialAttackPacket(CMSpecialAttack.DUAL_SWIPE, true));
    }
}