package me.Thelnfamous1.chainsaw_man.client.keymapping;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class CMOptions {
    public static final String SUMMON_FOX_DEVIL = "key." + ChainsawManMod.MODID + ".summon_fox_devil";
    public static final String MORPH_CHAINSAW_MAN = "key." + ChainsawManMod.MODID + ".morph_chainsaw_man";
    public static final String ATTACK_CHAINSAW_MAN = "key." + ChainsawManMod.MODID + ".attack_chainsaw_man";
    public static final String CATEGORY = "key." + ChainsawManMod.MODID + ".category";

    public final KeyBinding keySummonFoxDevil = new KeyBinding(SUMMON_FOX_DEVIL, GLFW.GLFW_KEY_V, CATEGORY);

    public final KeyBinding keyMorphChainsawMan = new KeyBinding(MORPH_CHAINSAW_MAN, GLFW.GLFW_KEY_Z, CATEGORY);

    public final KeyBinding keyAttackChainsawMan = new KeyBinding(ATTACK_CHAINSAW_MAN, GLFW.GLFW_KEY_X, CATEGORY);

}