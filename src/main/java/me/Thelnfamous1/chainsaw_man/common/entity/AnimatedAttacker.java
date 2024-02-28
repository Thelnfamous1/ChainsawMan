package me.Thelnfamous1.chainsaw_man.common.entity;

import com.google.common.collect.ImmutableList;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public interface AnimatedAttacker<A extends AnimatedAttacker.AttackType> {
    byte ATTACK_EVENT_ID = 4;

    static <A extends AnimatedAttacker.AttackType> Optional<A> optionalCurrentAttackType(AnimatedAttacker<A> attacker){
        return Optional.ofNullable(attacker.getCurrentAttackType());
    }

    static <A extends AnimatedAttacker.AttackType> boolean hasCurrentAttackType(AnimatedAttacker<A> attacker, A attackType){
        return attackType.equals(attacker.getCurrentAttackType());
    }

    static <A extends AnimatedAttacker.AttackType> float getAttackProgress(AnimatedAttacker<A> attacker){
        A attackType = attacker.getCurrentAttackType();
        if(attackType == null) return 0;

        int attackDuration = attackType.getAttackDuration();
        if(attackDuration <= 0) return 0;

        float ticksSinceAttackStarted = MathHelper.clamp((float)attacker.getTicksSinceAttackStarted(), 0.0F, attackDuration);
        return MathHelper.clamp(ticksSinceAttackStarted / attackDuration, 0.0F, 1.0F);
    }

    default boolean isAttackAnimationInProgress(){
        if(this.isAttacking()){
            A currentAttackType = this.getCurrentAttackType();
            return currentAttackType != null && this.getTicksSinceAttackStarted() <= currentAttackType.getAttackDuration();
        }
        return false;
    }

    boolean isAttacking();

    int getTicksSinceAttackStarted();

    @Nullable
    default AttackPoint getCurrentAttackPoint(){
        A currentAttackType = this.getCurrentAttackType();
        if(currentAttackType == null){
            return null;
        }
        for(AttackPoint attackPoint : this.getCurrentAttackType().getAttackPoints()){
            if(this.getTicksSinceAttackStarted() == attackPoint.getTicksSinceStart())
                return attackPoint;
        }
        return null;
    }

    @Nullable A getCurrentAttackType();

    void setCurrentAttackType(@Nullable A attackType);

    interface AttackType{
        int getId();

        ImmutableList<AttackPoint> getAttackPoints();

        int getAttackDuration();

        String getKey();

    }

    class AttackPoint{
        private final int ticksSinceStart;
        private final double baseDamageModifier;
        private final DamageMode damageMode;

        public AttackPoint(int ticksSinceStart, double baseDamageModifier, DamageMode damageMode) {
            this.ticksSinceStart = ticksSinceStart;
            this.baseDamageModifier = baseDamageModifier;
            this.damageMode = damageMode;
        }

        public static AttackPoint of(float secondsSinceStart, double baseDamageModifier, DamageMode damageMode){
            return of(CMUtil.secondsToTicks(secondsSinceStart), baseDamageModifier, damageMode);
        }
        public static AttackPoint of(int ticksSinceStart, double baseDamageModifier, DamageMode damageMode){
            return new AttackPoint(ticksSinceStart, baseDamageModifier, damageMode);
        }

        public int getTicksSinceStart() {
            return this.ticksSinceStart;
        }

        public double getBaseDamageModifier() {
            return this.baseDamageModifier;
        }

        public DamageMode getDamageMode() {
            return this.damageMode;
        }

        public enum DamageMode{
            MELEE,
            AREA_OF_EFFECT,
            RANGED
        }
    }
}