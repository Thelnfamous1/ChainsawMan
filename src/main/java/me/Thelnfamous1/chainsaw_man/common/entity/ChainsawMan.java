package me.Thelnfamous1.chainsaw_man.common.entity;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.AOEAttackHelper;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.loading.FMLEnvironment;
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

public class ChainsawMan extends CreatureEntity implements AnimatedAttacker<ChainsawManAttackType>, IAnimatable {
    protected static final DataParameter<OptionalInt> DATA_ATTACK_TYPE_ID = EntityDataManager.defineId(ChainsawMan.class, DataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final DataParameter<Boolean> DATA_ATTACKING = EntityDataManager.defineId(ChainsawMan.class, DataSerializers.BOOLEAN);
    private static final AnimationBuilder RIGHT_SWIPE_ANIM = new AnimationBuilder().addAnimation("animation.denji.attack");
    private static final AnimationBuilder LEFT_SWIPE_ANIM = new AnimationBuilder().addAnimation("animation.denji.attack2");
    private static final AnimationBuilder DUAL_SWIPE_ANIM = new AnimationBuilder().addAnimation("animation.denji.attack3");
    private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("animation.denji.walk", true);
    private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.denji.idle", true);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Nullable
    private ChainsawManAttackType currentAttackType;
    private int attackTicker;

    public ChainsawMan(EntityType<? extends ChainsawMan> type, World world) {
        super(type, world);
        this.maxUpStep = 3.0F;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
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

    public boolean startAttack(ChainsawManAttackType attackType) {
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

    protected void onAttackStarted(ChainsawManAttackType currentAttackType) {
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
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
        super.tick();
        this.tickAnimatedAttack();
    }

    public void tickAnimatedAttack() {
        ChainsawManAttackType currentAttackType = this.getCurrentAttackType();
        if(currentAttackType != null && this.isAttacking()){
            if(this.isAttackAnimationInProgress()){
                AttackPoint currentAttackPoint = this.getCurrentAttackPoint();
                if(currentAttackPoint != null){
                    this.executeAttack(currentAttackType, currentAttackPoint);
                }
                this.attackTicker++;
                if(FMLEnvironment.production) ChainsawManMod.LOGGER.info("{} has attack ticker of {} for {}", this, this.attackTicker, currentAttackType.getKey());
            } else{
                this.setCurrentAttackType(null);
                this.setAttacking(false);
            }
        }
    }

    protected void executeAttack(ChainsawManAttackType currentAttackType, AttackPoint currentAttackPoint){
        this.playAttackSound(currentAttackType, currentAttackPoint);
        switch (currentAttackPoint.getDamageMode()){
            case AREA_OF_EFFECT:
                AxisAlignedBB attackBox = AOEAttackHelper.createAttackBox(this, this.getAttackRadius(currentAttackType), this.xRot , this.getYHeadRot());
                if(!FMLEnvironment.production) AOEAttackHelper.sendHitboxParticles(attackBox, this.level);
                if(!this.level.isClientSide){
                    List<LivingEntity> targets = this.level.getNearbyEntities(LivingEntity.class, EntityPredicate.DEFAULT, this, attackBox);
                    targets.forEach(target -> this.morphDoHurtTarget(target, currentAttackPoint.getBaseDamageModifier()));
                }
                this.finalizeAreaOfEffectAttack(attackBox);
                break;
            case MELEE:
        }
    }

    protected void playAttackSound(ChainsawManAttackType currentAttackType, AttackPoint currentAttackPoint) {
    }

    protected Vector3d getAttackRadius(ChainsawManAttackType currentAttackType){
        switch (currentAttackType){
            case DUAL_SWIPE:
                return new Vector3d(3, 3, 5);
            default:
                return new Vector3d(3, 3, 3);
        }
    }

    protected void morphDoHurtTarget(Entity target, double baseDamageModifier){
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        attackDamage *= baseDamageModifier;
        float attackKnockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            LivingEntity livingTarget = (LivingEntity) target;
            attackDamage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), livingTarget.getMobType());
            attackKnockback += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        int fireAspect = EnchantmentHelper.getFireAspect(this);
        if (fireAspect > 0) {
            target.setSecondsOnFire(fireAspect * 4);
        }

        boolean hurt = target.hurt(CMUtil.getMorphDamageSource(this), attackDamage);
        if (hurt) {
            if (attackKnockback > 0.0F && target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                livingTarget.knockback(attackKnockback * 0.5F, MathHelper.sin(this.yRot * CMUtil.DEG_TO_RAD), -MathHelper.cos(this.yRot * CMUtil.DEG_TO_RAD));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }
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
    public ChainsawManAttackType getCurrentAttackType() {
        if(!this.level.isClientSide){
            return this.currentAttackType;
        } else{
            OptionalInt id = this.entityData.get(DATA_ATTACK_TYPE_ID);
            return id.isPresent() ? ChainsawManAttackType.byId(id.getAsInt()) : null;
        }
    }

    @Override
    public void setCurrentAttackType(@Nullable ChainsawManAttackType attackType) {
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, attackType == null ? OptionalInt.empty() : OptionalInt.of(attackType.getId()));
    }

    @Override
    public boolean canRotateDuringAttack(ChainsawManAttackType currentAttackType) {
        return false;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "Attack", 0, this::attack));
        animationData.addAnimationController(new AnimationController<>(this, "Move", 0, this::move));
    }

    private <E extends ChainsawMan> PlayState attack(AnimationEvent<E> event) {
        if (this.isAttackAnimationInProgress()) {
            //noinspection ConstantConditions
            switch (this.getCurrentAttackType()){
                case RIGHT_SWIPE:
                    event.getController().setAnimation(RIGHT_SWIPE_ANIM);
                    return PlayState.CONTINUE;
                case LEFT_SWIPE:
                    event.getController().setAnimation(LEFT_SWIPE_ANIM);
                    return PlayState.CONTINUE;
                case DUAL_SWIPE:
                    event.getController().setAnimation(DUAL_SWIPE_ANIM);
                    return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    private <E extends ChainsawMan> PlayState move(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        } else if(!this.isAttackAnimationInProgress()){
            event.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
