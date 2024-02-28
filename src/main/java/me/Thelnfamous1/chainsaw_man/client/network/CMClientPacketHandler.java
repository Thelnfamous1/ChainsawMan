package me.Thelnfamous1.chainsaw_man.client.network;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.network.ClientboundSpecialAttackPacket;
import me.ichun.mods.morph.common.morph.MorphHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CMClientPacketHandler {

    public void handleSpecialAttackPacket(ClientboundSpecialAttackPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.getId());
        if(entity instanceof PlayerEntity){
            CMSpecialAttack attackType = packet.getAttack();
            LivingEntity activeMorphEntity = MorphHandler.INSTANCE.getActiveMorphEntity((PlayerEntity) entity);
            if(activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()){
                attackType.getChainsawAttack().accept(((ChainsawMan)activeMorphEntity));
            }
        }
    }
}
