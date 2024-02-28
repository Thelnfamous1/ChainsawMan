package me.Thelnfamous1.chainsaw_man.common.ability;

import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawManAttackType;

public enum CMSpecialAttack {
    RIGHT_SWIPE((denji, force) -> denji.startAttack(ChainsawManAttackType.RIGHT_SWIPE, force)),
    LEFT_SWIPE((denji, force) -> denji.startAttack(ChainsawManAttackType.LEFT_SWIPE, force)),
    DUAL_SWIPE((denji, force) -> denji.startAttack(ChainsawManAttackType.DUAL_SWIPE, force));

    private final SpecialAttackExecution execution;

    CMSpecialAttack(SpecialAttackExecution execution){
        this.execution = execution;
    }

    public static CMSpecialAttack byAttackType(ChainsawManAttackType attackType){
        switch (attackType){
            case RIGHT_SWIPE:
                return RIGHT_SWIPE;
            case LEFT_SWIPE:
                return LEFT_SWIPE;
            case DUAL_SWIPE:
                return DUAL_SWIPE;
            default:
                throw new IllegalArgumentException("Invalid ChainsawManAttackType: " + attackType);
        }
    }

    public SpecialAttackExecution getExecution() {
        return this.execution;
    }

    @FunctionalInterface
    public interface SpecialAttackExecution{
        boolean apply(ChainsawMan chainsawMan, boolean force);
    }
}