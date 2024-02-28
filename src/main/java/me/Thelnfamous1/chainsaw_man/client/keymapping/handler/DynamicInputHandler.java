package me.Thelnfamous1.chainsaw_man.client.keymapping.handler;

import me.Thelnfamous1.chainsaw_man.client.keymapping.InputHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

public abstract class DynamicInputHandler implements InputHandler {
    private boolean keyDown;

    @Override
    public void apply(ClientPlayerEntity player, KeyBinding key) {
        boolean wasKeyDown = this.keyDown;
        this.keyDown = key.isDown();
        if (wasKeyDown && !this.keyDown) {
            this.onKeyRelease();
        } else if (!wasKeyDown && this.keyDown) {
            this.onKeyFirstPress();
        } else if (wasKeyDown) {
            this.onKeyHeld();
        }
    }

    protected void onKeyHeld() {
    }

    protected void onKeyFirstPress() {
    }

    protected void onKeyRelease() {
    }
}
