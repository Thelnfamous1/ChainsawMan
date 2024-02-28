package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.client.ChainsawManModClient;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundStartSpecialAttackPacket {
    private final int id;
    private final CMSpecialAttack attack;

    public ClientboundStartSpecialAttackPacket(Entity entity, CMSpecialAttack attack) {
        this(entity.getId(), attack);
    }

    private ClientboundStartSpecialAttackPacket(int entityId, CMSpecialAttack attack) {
        this.id = entityId;
        this.attack = attack;
    }

    public static ClientboundStartSpecialAttackPacket decode(PacketBuffer packetBuffer) {
        int id = packetBuffer.readInt();
        CMSpecialAttack chainsawAttack = packetBuffer.readEnum(CMSpecialAttack.class);
        return new ClientboundStartSpecialAttackPacket(id, chainsawAttack);
    }

    public static void encode(ClientboundStartSpecialAttackPacket packet, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(packet.id);
        packetBuffer.writeEnum(packet.attack);
    }

    public static void handle(ClientboundStartSpecialAttackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ChainsawManModClient.getPacketHandler().handleSpecialAttackPacket(packet));
        ctx.get().setPacketHandled(true);
    }

    public int getId() {
        return this.id;
    }

    public CMSpecialAttack getAttack() {
        return this.attack;
    }
}
