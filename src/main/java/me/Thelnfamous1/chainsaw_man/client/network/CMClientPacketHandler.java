package me.Thelnfamous1.chainsaw_man.client.network;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.CMMorphHelper;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.network.ClientboundStopSpecialAttackPacket;
import me.Thelnfamous1.chainsaw_man.common.network.ClientboundStartSpecialAttackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CMClientPacketHandler {

    public void handleSpecialAttackPacket(ClientboundStartSpecialAttackPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.getId());
        if(entity instanceof PlayerEntity){
            CMSpecialAttack attackType = packet.getAttack();
            LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity((PlayerEntity) entity);
            if(activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()){
                attackType.getExecution().apply(((ChainsawMan)activeMorphEntity), true);
            }
        }
    }

    public void handleStopSpecialAttackPacket(ClientboundStopSpecialAttackPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.getId());
        if(entity instanceof PlayerEntity){
            LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity((PlayerEntity) entity);
            if(activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()){
                ((ChainsawMan)activeMorphEntity).stopAttacking();
            }
        }
    }
}
