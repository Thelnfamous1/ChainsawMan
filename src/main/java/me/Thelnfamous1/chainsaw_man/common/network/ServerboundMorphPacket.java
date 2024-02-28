package me.Thelnfamous1.chainsaw_man.common.network;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.ability.CMMorph;
import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundMorphPacket {
    private final CMMorph morph;

    public ServerboundMorphPacket(CMMorph morph){
        this.morph = morph;
    }

    public static ServerboundMorphPacket decode(PacketBuffer packetBuffer){
        CMMorph morph = packetBuffer.readEnum(CMMorph.class);
        return new ServerboundMorphPacket(morph);
    }

    public static void encode(ServerboundMorphPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.morph);
    }

    public static void handle(ServerboundMorphPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;
            if(!serverPlayer.level.getGameRules().getBoolean(ChainsawManMod.getRuleChainsawMan())) return;
            World world = serverPlayer.level;

            CMMorph morph = packet.getMorph();

            if(MorphHandler.INSTANCE.getMorphInfo(serverPlayer).isCurrentlyThisVariant(morph.getMorphVariant(world).thisVariant)){
                MorphHandler.INSTANCE.demorph(serverPlayer);
            } else{
                MorphHandler.INSTANCE.morphTo(serverPlayer, morph.getMorphVariant(world));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public CMMorph getMorph() {
        return this.morph;
    }

}