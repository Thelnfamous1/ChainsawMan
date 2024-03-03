package me.Thelnfamous1.chainsaw_man.common.entity;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class ChainsawSweep extends Entity implements IAnimatable, IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> DATA_TEXTURE_ID = EntityDataManager.defineId(ChainsawSweep.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_LEFT_HIDDEN = EntityDataManager.defineId(ChainsawSweep.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_RIGHT_HIDDEN = EntityDataManager.defineId(ChainsawSweep.class, DataSerializers.BOOLEAN);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int lifetime;
    private UUID ownerUUID;
    private int ownerNetworkId;

    public ChainsawSweep(EntityType<? extends ChainsawSweep> pType, World pLevel) {
        super(pType, pLevel);
        this.noPhysics = true;
        this.lifetime = 4;
        this.setTexture(this.getTextureFromAge(this.tickCount));
    }

    public ChainsawSweep(World pLevel, LivingEntity owner) {
        this(ChainsawManMod.CHAINSAW_SWEEP.get(), pLevel);
        this.setOwner(owner);
        this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.yRot, owner.xRot);
        this.setRot(owner.yRot, owner.xRot);
    }

    public void setOwner(@Nullable Entity pEntity) {
        if (pEntity != null) {
            this.ownerUUID = pEntity.getUUID();
            this.ownerNetworkId = pEntity.getId();
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level instanceof ServerWorld) {
            return ((ServerWorld)this.level).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level.getEntity(this.ownerNetworkId) : null;
        }
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_TEXTURE_ID, 0);
        this.getEntityData().define(DATA_LEFT_HIDDEN, false);
        this.getEntityData().define(DATA_RIGHT_HIDDEN, false);
    }

    public void setLeftHidden(boolean leftHidden){
        this.entityData.set(DATA_LEFT_HIDDEN, leftHidden);
    }

    public boolean isLeftHidden(){
        return this.entityData.get(DATA_LEFT_HIDDEN);
    }

    public void setRightHidden(boolean rightHidden){
        this.entityData.set(DATA_RIGHT_HIDDEN, rightHidden);
    }

    public boolean isRightHidden(){
        return this.entityData.get(DATA_RIGHT_HIDDEN);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = this.getOwner();
        if(!FMLEnvironment.production) ChainsawManMod.LOGGER.info("Left sweep is hidden ? {}, right sweep is hidden ? {}", this.isLeftHidden(), this.isRightHidden());
        if(owner != null){
            this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.yRot, owner.xRot);
        }
        int age = this.tickCount - 1;
        if (age >= this.lifetime && !this.level.isClientSide) {
            this.remove();
        } else {
            this.setTexture(this.getTextureFromAge(age));
        }
    }

    private int getTextureFromAge(int age) {
        int maxIndex = this.getTextureCount() - 1;
        return Math.min(maxIndex, age * (maxIndex) / this.lifetime);
    }

    private int getTextureCount() {
        return 8;
    }

    private void setTexture(int textureFromAge) {
        this.entityData.set(DATA_TEXTURE_ID, textureFromAge);
    }

    public int getTexture(){
        return this.entityData.get(DATA_TEXTURE_ID);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        this.lifetime = pCompound.getInt("Lifetime");
        this.tickCount = pCompound.getInt("Age");
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        pCompound.putInt("Age", this.tickCount);
        pCompound.putInt("Lifetime", this.lifetime);
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        Entity owner = this.getOwner();
        buffer.writeVarInt(owner != null ? owner.getId() : 0);
        buffer.writeBoolean(this.isLeftHidden());
        buffer.writeBoolean(this.isRightHidden());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int ownerId = additionalData.readVarInt();
        if(ownerId > 0){
            this.setOwner(this.level.getEntity(ownerId));
        }
        this.setLeftHidden(additionalData.readBoolean());
        this.setRightHidden(additionalData.readBoolean());
    }
}
