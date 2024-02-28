package me.Thelnfamous1.chainsaw_man.client.keymapping;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

import java.util.LinkedHashMap;
import java.util.Map;

public class CMInput {

    private final Map<KeyBinding, InputHandler> inputHandlers = new LinkedHashMap<>();

    public CMInput(){
    }

    public void registerInputHandler(KeyBinding keyMapping, InputHandler inputHandler){
        this.inputHandlers.put(keyMapping, inputHandler);
    }

    public void tick(ClientPlayerEntity player){
        this.inputHandlers.forEach((key, value) -> value.apply(player, key));
    }

}