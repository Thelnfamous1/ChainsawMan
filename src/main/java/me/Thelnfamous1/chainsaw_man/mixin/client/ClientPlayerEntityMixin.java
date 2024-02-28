package me.Thelnfamous1.chainsaw_man.mixin.client;

import me.Thelnfamous1.chainsaw_man.client.ChainsawManModClient;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;aiStep()V"))
    private void handleAiStep(CallbackInfo ci){
        ChainsawManModClient.getInput().tick((ClientPlayerEntity) (Object)this);
    }
}