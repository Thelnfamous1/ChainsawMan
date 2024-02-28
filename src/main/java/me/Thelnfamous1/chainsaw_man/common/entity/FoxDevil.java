package me.Thelnfamous1.chainsaw_man.common.entity;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.AOEAttackHelper;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

public class FoxDevil extends DamagingProjectileEntity implements IEntityAdditionalSpawnData, IAnimatable, AnimatedAttacker<FoxDevilAttackType> {
    protected static final DataParameter<OptionalInt> DATA_ATTACK_TYPE_ID = EntityDataManager.defineId(FoxDevil.class, DataSerializers.OPTIONAL_UNSIGNED_INT);

    private static final DataParameter<Boolean> DATA_ATTACKING = EntityDataManager.defineId(FoxDevil.class, DataSerializers.BOOLEAN);
    public static final float ATTACK_DAMAGE = 15.0F;
    public static final AnimationBuilder ATTACK_ANIM = new AnimationBuilder().addAnimation("animation.kon.attack", false);
    public static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.kon.idle", true);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Nullable
    private FoxDevilAttackType currentAttackType;
    private int attackTicker;
    private int life;
    private int attackDelay;

    public FoxDevil(EntityType<? extends FoxDevil> type, World level) {
        super(type, level);
    }

    public FoxDevil(World world, LivingEntity owner, double xDist, double yDist, double zDist) {
        super(ChainsawManMod.FOX_DEVIL.get(), owner, xDist, yDist, zDist, world);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected IParticleData getTrailParticle() {
        return ParticleTypes.POOF;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACKING, false);
        this.entityData.define(DATA_ATTACK_TYPE_ID, OptionalInt.empty());
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if(dataAccessor.equals(DATA_ATTACKING)){
            this.attackTicker = 0;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putShort("life", (short)this.life);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.life = pCompound.getShort("life");
    }

    public boolean startAttack(FoxDevilAttackType attackType) {
        if(!this.isAttackAnimationInProgress()){
            this.setAttacking(true);
            this.setCurrentAttackType(attackType);
            this.onAttackStarted(this.getCurrentAttackType());
            return true;
        }
        return false;
    }

    protected void setAttacking(boolean attacking) {
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    protected void onAttackStarted(FoxDevilAttackType currentAttackType) {
        this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.0F, 1.0F);
    }

    @Override
    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    @Override
    public int getTicksSinceAttackStarted() {
        return this.attackTicker;
    }

    @Override
    public void tick() {
        /*
        float oldXRot0 = this.xRotO;
        float oldXRot = this.xRot;
        float oldYRot0 = this.yRotO;
        float oldYRot = this.yRot;
         */
        if(!this.isAttacking() || this.attackDelay > 0){
            this.setDeltaMovement(Vector3d.ZERO);
        }
        super.tick();
        if(!this.isAttacking() || this.attackDelay > 0){
            this.setDeltaMovement(Vector3d.ZERO);
            CMUtil.rotateTowardsMovement(this, 1, new Vector3d(this.xPower, this.yPower, this.zPower));
        }
        if(this.attackDelay > 0){
            this.attackDelay--;
            if(this.attackDelay == 0 && !this.level.isClientSide){
                this.startAttack(FoxDevilAttackType.BITE);
            }
        }
        this.tickAnimatedAttack();
        if (!this.level.isClientSide && this.attackDelay <= 0 && !this.isAttacking()) {
            this.tickDespawn();
        }
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 20) {
            this.remove();
            if(this.level instanceof ServerWorld){
                ((ServerWorld)this.level).sendParticles(ParticleTypes.POOF,
                        this.getX(), this.getY(), this.getZ(),
                        20,
                        this.getBbWidth() * 0.5F, this.getBbHeight() * 0.5F, this.getBbWidth() * 0.5F,
                        0.02D);
            }
        }

    }

    public void tickAnimatedAttack() {
        FoxDevilAttackType currentAttackType = this.getCurrentAttackType();
        if(currentAttackType != null && this.isAttacking()){
            if(this.isAttackAnimationInProgress()){
                AttackPoint currentAttackPoint = this.getCurrentAttackPoint();
                if(currentAttackPoint != null){
                    this.executeAttack(currentAttackType, currentAttackPoint);
                }
                this.attackTicker++;
                //if(!FMLEnvironment.production) ChainsawManMod.LOGGER.info("{} has attack ticker of {} for {}", this, this.attackTicker, currentAttackType.getKey());
            } else{
                this.setCurrentAttackType(null);
                this.setAttacking(false);
            }
        }
    }

    protected void executeAttack(FoxDevilAttackType currentAttackType, AttackPoint currentAttackPoint){
        this.playAttackSound(currentAttackType, currentAttackPoint);
        switch (currentAttackPoint.getDamageMode()){
            case AREA_OF_EFFECT:
                AxisAlignedBB attackBox = AOEAttackHelper.createProjectileAttackBox(this, this.getAttackRadii(currentAttackType), new Vector3d(this.xPower, this.yPower, this.zPower));
                if(!FMLEnvironment.production) AOEAttackHelper.sendHitboxParticles(attackBox, this.level);
                if(!this.level.isClientSide){
                    Entity owner = this.getOwner();
                    LivingEntity livingOwner = owner instanceof LivingEntity ? (LivingEntity) owner : null;
                    //noinspection ConstantConditions
                    List<LivingEntity> targets = this.level.getNearbyEntities(LivingEntity.class, EntityPredicate.DEFAULT, livingOwner, attackBox);
                    targets.forEach(target -> this.doHurtTarget(target, currentAttackPoint.getBaseDamageModifier(), livingOwner));
                }
                this.finalizeAreaOfEffectAttack(attackBox);
                break;
        }
    }

    protected void playAttackSound(FoxDevilAttackType currentAttackType, AttackPoint currentAttackPoint) {
        this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
    }

    protected Vector3d getAttackRadii(FoxDevilAttackType currentAttackType){
        return new Vector3d(5, 5, 5);
    }

    protected void doHurtTarget(Entity target, double damageModifier, @Nullable LivingEntity owner){
        float damage = (float) (this.getBaseDamage() * damageModifier);
        if (owner != null) {
            boolean hurt = target.hurt(DamageSource.indirectMagic(this, owner), damage);
            if (hurt) {
                if (target.isAlive()) {
                    this.doEnchantDamageEffects(owner, target);
                }
            }
        } else {
            target.hurt(DamageSource.MAGIC, damage);
        }
    }

    private double getBaseDamage() {
        return ATTACK_DAMAGE;
    }

    protected void finalizeAreaOfEffectAttack(AxisAlignedBB attackBox) {
        if(!this.level.isClientSide){
            Vector3d center = attackBox.getCenter();
            double radius = attackBox.getSize() * 0.5;
            Vector3d particlePos = center.subtract(0, (attackBox.getYsize() * 0.5D) - 0.5D, 0);
            //CMUtil.spawnVanillaExplosionParticles(((ServerWorld) this.level), radius, particlePos);
        }
    }

    @Nullable
    @Override
    public FoxDevilAttackType getCurrentAttackType() {
        if(!this.level.isClientSide){
            return this.currentAttackType;
        } else{
            OptionalInt id = this.entityData.get(DATA_ATTACK_TYPE_ID);
            return id.isPresent() ? FoxDevilAttackType.byId(id.getAsInt()) : null;
        }
    }

    @Override
    public void setCurrentAttackType(@Nullable FoxDevilAttackType attackType) {
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, attackType == null ? OptionalInt.empty() : OptionalInt.of(attackType.getId()));
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        Entity owner = this.getOwner();
        buffer.writeInt(owner != null ? owner.getId() : 0);
        buffer.writeShort((int)(MathHelper.clamp(this.xPower, -3.9D, 3.9D) * 8000.0D));
        buffer.writeShort((int)(MathHelper.clamp(this.yPower, -3.9D, 3.9D) * 8000.0D));
        buffer.writeShort((int)(MathHelper.clamp(this.zPower, -3.9D, 3.9D) * 8000.0D));
        /*
        FoxDevilAttackType currentAttackType = this.getCurrentAttackType();
        buffer.writeBoolean(currentAttackType != null);
        if(currentAttackType != null){
            buffer.writeEnum(currentAttackType);
        }
         */
        buffer.writeInt(this.attackDelay);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int ownerId = additionalData.readInt();
        if(ownerId > 0){
            this.setOwner(this.level.getEntity(ownerId));
        }
        this.xPower = additionalData.readShort() / 8000.0D;
        this.yPower = additionalData.readShort() / 8000.0D;
        this.zPower = additionalData.readShort() / 8000.0D;

        /*
        if(additionalData.readBoolean()){
            this.startAttack(additionalData.readEnum(FoxDevilAttackType.class));
        }
         */
        this.attackDelay = additionalData.readInt();
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends FoxDevil> PlayState predicate(AnimationEvent<E> event) {
        if (this.isAttackAnimationInProgress()) {
            event.getController().setAnimation(ATTACK_ANIM);
        } else{
            event.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }
}
