package me.Thelnfamous1.chainsaw_man.client.keymapping;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

@FunctionalInterface
public interface InputHandler {
    void apply(ClientPlayerEntity player, KeyBinding key);
}