package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.ability.ChainsawAttack;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ServerboundSpecialAttackPacket {
    private final ChainsawAttack attack;

    public ServerboundSpecialAttackPacket(ChainsawAttack attack){
        this.attack = attack;
    }

    public static ServerboundSpecialAttackPacket decode(PacketBuffer packetBuffer){
        ChainsawAttack chainsawAttack = packetBuffer.readEnum(ChainsawAttack.class);
        return new ServerboundSpecialAttackPacket(chainsawAttack);
    }

    public static void encode(ServerboundSpecialAttackPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.attack);
    }

    public static void handle(ServerboundSpecialAttackPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;

            ChainsawAttack attackType = packet.getAttack();

            LivingEntity activeMorphEntity = MorphHandler.INSTANCE.getActiveMorphEntity(serverPlayer);
            if(activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()){
                attackType.getChainsawAttack().accept(((ChainsawMan)activeMorphEntity));
                ChainsawManMod.NETWORK_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new ClientboundSpecialAttackPacket(serverPlayer, packet.attack));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public ChainsawAttack getAttack() {
        return this.attack;
    }

}