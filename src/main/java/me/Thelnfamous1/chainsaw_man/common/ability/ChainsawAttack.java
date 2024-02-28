package me.Thelnfamous1.chainsaw_man.common.ability;

import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawManAttackType;

import java.util.function.Consumer;

public enum ChainsawAttack {
    RIGHT_SWIPE(denji -> {
        denji.startAttack(ChainsawManAttackType.RIGHT_SWIPE);
    }),
    LEFT_SWIPE(denji -> {
        denji.startAttack(ChainsawManAttackType.LEFT_SWIPE);
    }),
    DUAL_SWIPE(denji -> {
        denji.startAttack(ChainsawManAttackType.DUAL_SWIPE);
    });

    private final Consumer<ChainsawMan> chainsawAttack;

    ChainsawAttack(Consumer<ChainsawMan> chainsawAttack){

        this.chainsawAttack = chainsawAttack;
    }

    public Consumer<ChainsawMan> getChainsawAttack() {
        return this.chainsawAttack;
    }
}