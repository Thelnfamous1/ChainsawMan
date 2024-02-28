package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.client.keymapping.InputHandler;
import me.Thelnfamous1.chainsaw_man.common.ability.CMAbility;
import me.Thelnfamous1.chainsaw_man.common.network.KeyBindAction;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundAbilityPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

public class SummonFoxDevilHandler implements InputHandler {

    public static final SummonFoxDevilHandler INSTANCE = new SummonFoxDevilHandler();
    private boolean summoning;

    private SummonFoxDevilHandler() {

    }

    @Override
    public void apply(ClientPlayerEntity player, KeyBinding key) {
        boolean wasSummoning = this.summoning;
        this.summoning = key.isDown();
        if (wasSummoning && !this.summoning) {
        } else if (!wasSummoning && this.summoning) {
            ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundAbilityPacket(KeyBindAction.INITIAL_PRESS, CMAbility.SUMMON_FOX_DEVIL));
        } else if (wasSummoning) {
        }
    }
}