package me.Thelnfamous1.chainsaw_man.common.entity;

import com.google.common.collect.ImmutableList;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;
import net.minecraft.util.HandSide;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

public enum ChainsawManAttackType implements AnimatedAttacker.AttackType {
    RIGHT_SWIPE(0, "right_swipe", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.29F, 0, AnimatedAttacker.AttackPoint.DamageMode.VFX),
            AnimatedAttacker.AttackPoint.of(0.42F, 1, AnimatedAttacker.AttackPoint.DamageMode.MELEE)),
            CMUtil.secondsToTicks(1F)),
    LEFT_SWIPE(1, "left_swipe", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.29F, 0, AnimatedAttacker.AttackPoint.DamageMode.VFX),
            AnimatedAttacker.AttackPoint.of(0.42F, 1, AnimatedAttacker.AttackPoint.DamageMode.MELEE)),
            CMUtil.secondsToTicks(1.25F)),
    DUAL_SWIPE(2, "dual_swipe", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.42F, 0, AnimatedAttacker.AttackPoint.DamageMode.VFX),
            AnimatedAttacker.AttackPoint.of(0.63F, 1, AnimatedAttacker.AttackPoint.DamageMode.AREA_OF_EFFECT)),
            CMUtil.secondsToTicks(1.2917F));

    private static final ChainsawManAttackType[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ChainsawManAttackType::getId)).toArray(ChainsawManAttackType[]::new);

    private final int id;
    private final String key;
    private final ImmutableList<AnimatedAttacker.AttackPoint> attackPoints;
    private final int attackDuration;

    ChainsawManAttackType(int id, String key, ImmutableList<AnimatedAttacker.AttackPoint> attackPoints, int attackDuration) {
        this.id = id;
        this.key = key;
        this.attackPoints = attackPoints;
        this.attackDuration = attackDuration;
    }

    public static ChainsawManAttackType byId(int id){
        return BY_ID[id % BY_ID.length];
    }

    @Nullable
    public static ChainsawManAttackType byName(String pName) {
        for(ChainsawManAttackType attackType : values()) {
            if (attackType.key.equals(pName)) {
                return attackType;
            }
        }

        return null;
    }

    public static ChainsawManAttackType byMainArm(HandSide mainArm) {
        return mainArm == HandSide.RIGHT ? RIGHT_SWIPE : LEFT_SWIPE;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ImmutableList<AnimatedAttacker.AttackPoint> getAttackPoints() {
        return this.attackPoints;
    }

    @Override
    public int getAttackDuration() {
        return this.attackDuration;
    }

    @Override
    public String getKey() {
        return this.key;
    }

}