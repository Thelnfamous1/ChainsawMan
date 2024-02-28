package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.CMMorphHelper;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ServerboundSpecialAttackPacket {
    private final CMSpecialAttack attack;
    private final boolean updateClient;

    public ServerboundSpecialAttackPacket(CMSpecialAttack attack, boolean updateClient){
        this.attack = attack;
        this.updateClient = updateClient;
    }

    public static ServerboundSpecialAttackPacket decode(PacketBuffer packetBuffer){
        CMSpecialAttack chainsawAttack = packetBuffer.readEnum(CMSpecialAttack.class);
        boolean updateClient = packetBuffer.readBoolean();
        return new ServerboundSpecialAttackPacket(chainsawAttack, updateClient);
    }

    public static void encode(ServerboundSpecialAttackPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.attack);
        packetBuffer.writeBoolean(packet.updateClient);
    }

    public static void handle(ServerboundSpecialAttackPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;

            CMSpecialAttack attackType = packet.getAttack();

            LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity(serverPlayer);
            if(activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()){
                boolean applied = attackType.getExecution().apply(((ChainsawMan)activeMorphEntity), false);
                if(!applied){
                    ChainsawManMod.NETWORK_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new ClientboundStopSpecialAttackPacket(serverPlayer));
                } else if(packet.isUpdateClient())
                    ChainsawManMod.NETWORK_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new ClientboundStartSpecialAttackPacket(serverPlayer, packet.attack));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public CMSpecialAttack getAttack() {
        return this.attack;
    }

    public boolean isUpdateClient() {
        return this.updateClient;
    }
}