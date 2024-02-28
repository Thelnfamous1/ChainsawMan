package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.client.keymapping.InputHandler;
import me.Thelnfamous1.chainsaw_man.common.ability.CMMorph;
import me.Thelnfamous1.chainsaw_man.common.network.ServerboundMorphPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

public class MorphChainsawManHandler implements InputHandler {

    public static final MorphChainsawManHandler INSTANCE = new MorphChainsawManHandler();
    private boolean morph;

    private MorphChainsawManHandler() {

    }

    @Override
    public void apply(ClientPlayerEntity player, KeyBinding key) {
        boolean wasMorph = this.morph;
        this.morph = key.isDown();
        if (wasMorph && !this.morph) {
        } else if (!wasMorph && this.morph) {
            ChainsawManMod.NETWORK_CHANNEL.sendToServer(new ServerboundMorphPacket(CMMorph.CHAINSAW_MAN));
        } else if (wasMorph) {
        }
    }
}