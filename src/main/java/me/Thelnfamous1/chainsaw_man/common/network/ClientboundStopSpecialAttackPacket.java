package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.client.ChainsawManModClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundStopSpecialAttackPacket {
    private final int id;

    public ClientboundStopSpecialAttackPacket(Entity entity) {
        this(entity.getId());
    }

    private ClientboundStopSpecialAttackPacket(int entityId) {
        this.id = entityId;
    }

    public static ClientboundStopSpecialAttackPacket decode(PacketBuffer packetBuffer) {
        int id = packetBuffer.readInt();
        return new ClientboundStopSpecialAttackPacket(id);
    }

    public static void encode(ClientboundStopSpecialAttackPacket packet, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(packet.id);
    }

    public static void handle(ClientboundStopSpecialAttackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ChainsawManModClient.getPacketHandler().handleStopSpecialAttackPacket(packet));
        ctx.get().setPacketHandled(true);
    }

    public int getId() {
        return this.id;
    }
}
