package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.client.ChainsawManModClient;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSpecialAttackPacket {
    private final int id;
    private final CMSpecialAttack attack;

    public ClientboundSpecialAttackPacket(Entity entity, CMSpecialAttack attack) {
        this(entity.getId(), attack);
    }

    private ClientboundSpecialAttackPacket(int entityId, CMSpecialAttack attack) {
        this.id = entityId;
        this.attack = attack;
    }

    public static ClientboundSpecialAttackPacket decode(PacketBuffer packetBuffer) {
        int id = packetBuffer.readInt();
        CMSpecialAttack chainsawAttack = packetBuffer.readEnum(CMSpecialAttack.class);
        return new ClientboundSpecialAttackPacket(id, chainsawAttack);
    }

    public static void encode(ClientboundSpecialAttackPacket packet, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(packet.id);
        packetBuffer.writeEnum(packet.attack);
    }

    public static void handle(ClientboundSpecialAttackPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
