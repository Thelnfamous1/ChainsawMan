package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.common.ability.CMAbility;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundAbilityPacket {
    private final KeyBindAction keyBindAction;
    private final CMAbility ability;

    public ServerboundAbilityPacket(KeyBindAction keyBindAction, CMAbility ability){
        this.keyBindAction = keyBindAction;
        this.ability = ability;
    }

    public static ServerboundAbilityPacket decode(PacketBuffer packetBuffer){
        KeyBindAction keyBindAction = packetBuffer.readEnum(KeyBindAction.class);
        CMAbility ability = packetBuffer.readEnum(CMAbility.class);
        return new ServerboundAbilityPacket(keyBindAction, ability);
    }

    public static void encode(ServerboundAbilityPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.keyBindAction);
        packetBuffer.writeEnum(packet.ability);
    }

    public static void handle(ServerboundAbilityPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;


            CMAbility ability = packet.getAbility();
            ability.getHandlerForKeyBindAction(packet.getKeyBindAction()).accept(serverPlayer);
        });
        ctx.get().setPacketHandled(true);
    }

    public KeyBindAction getKeyBindAction() {
        return this.keyBindAction;
    }

    public CMAbility getAbility() {
        return this.ability;
    }

}