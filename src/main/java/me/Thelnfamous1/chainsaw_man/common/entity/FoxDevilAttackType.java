package me.Thelnfamous1.chainsaw_man.common.entity;

import com.google.common.collect.ImmutableList;
import me.Thelnfamous1.chainsaw_man.common.CMUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

public enum FoxDevilAttackType implements AnimatedAttacker.AttackType{
    BITE(0, "bite", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.5F, 1, AnimatedAttacker.AttackPoint.DamageMode.AREA_OF_EFFECT)),
            CMUtil.secondsToTicks(1.4617F));

    private static final FoxDevilAttackType[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(FoxDevilAttackType::getId)).toArray(FoxDevilAttackType[]::new);

    private final int id;
    private final String key;
    private final ImmutableList<AnimatedAttacker.AttackPoint> attackPoints;
    private final int attackDuration;

    FoxDevilAttackType(int id, String key, ImmutableList<AnimatedAttacker.AttackPoint> attackPoints, int attackDuration) {
        this.id = id;
        this.key = key;
        this.attackPoints = attackPoints;
        this.attackDuration = attackDuration;
    }

    public static FoxDevilAttackType byId(int id){
        return BY_ID[id % BY_ID.length];
    }

    @Nullable
    public static FoxDevilAttackType byName(String pName) {
        for(FoxDevilAttackType attackType : values()) {
            if (attackType.key.equals(pName)) {
                return attackType;
            }
        }

        return null;
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
