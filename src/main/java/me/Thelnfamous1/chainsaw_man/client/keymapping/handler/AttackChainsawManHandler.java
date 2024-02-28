package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.client.keymapping.InputHandler;
import me.Thelnfamous1.chainsaw_man.common.ability.ChainsawAttack;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundSpecialAttackPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

public class AttackChainsawManHandler implements InputHandler {

    public static final AttackChainsawManHandler INSTANCE = new AttackChainsawManHandler();
    private boolean attack;

    private AttackChainsawManHandler() {

    }

    @Override
    public void apply(ClientPlayerEntity player, KeyBinding key) {
        boolean wasAttack = this.attack;
        this.attack = key.isDown();
        if (wasAttack && !this.attack) {
        } else if (!wasAttack && this.attack) {
            ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundSpecialAttackPacket(ChainsawAttack.DUAL_SWIPE));
        } else if (wasAttack) {
        }
    }
}