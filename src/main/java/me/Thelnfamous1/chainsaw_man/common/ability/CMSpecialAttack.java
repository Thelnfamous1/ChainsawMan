package me.Thelnfamous1.chainsaw_man.common.ability;

import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawManAttackType;

import java.util.function.Consumer;

public enum CMSpecialAttack {
    RIGHT_SWIPE(denji -> denji.startAttack(ChainsawManAttackType.RIGHT_SWIPE)),
    LEFT_SWIPE(denji -> denji.startAttack(ChainsawManAttackType.LEFT_SWIPE)),
    DUAL_SWIPE(denji -> denji.startAttack(ChainsawManAttackType.DUAL_SWIPE));

    private final Consumer<ChainsawMan> chainsawAttack;

    CMSpecialAttack(Consumer<ChainsawMan> chainsawAttack){
        this.chainsawAttack = chainsawAttack;
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

    public Consumer<ChainsawMan> getChainsawAttack() {
        return this.chainsawAttack;
    }
}