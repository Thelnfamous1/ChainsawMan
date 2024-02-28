package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.ability.CMMorph;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundMorphPacket;

public class MorphChainsawManHandler extends DynamicInputHandler {

    public static final MorphChainsawManHandler INSTANCE = new MorphChainsawManHandler();

    private MorphChainsawManHandler(){
    }

    @Override
    protected void onKeyFirstPress() {
        ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundMorphPacket(CMMorph.CHAINSAW_MAN));
    }
}