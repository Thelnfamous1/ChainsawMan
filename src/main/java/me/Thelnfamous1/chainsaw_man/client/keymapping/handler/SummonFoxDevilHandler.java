package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.ability.CMAbility;
import me.Thelnfamous1.chainsaw_man.common.network.KeyBindAction;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundAbilityPacket;

public class SummonFoxDevilHandler extends DynamicInputHandler {

    public static final SummonFoxDevilHandler INSTANCE = new SummonFoxDevilHandler();

    private SummonFoxDevilHandler() {
    }

    @Override
    protected void onKeyFirstPress() {
        ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundAbilityPacket(KeyBindAction.INITIAL_PRESS, CMAbility.SUMMON_FOX_DEVIL));
    }
}