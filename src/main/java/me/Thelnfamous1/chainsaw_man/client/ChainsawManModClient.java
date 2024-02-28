package me.Thelnfamous1.chainsaw_man.client;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.client.keymapping.CMInput;
import me.Thelnfamous1.chainsaw_man.client.keymapping.CMOptions;
import me.Thelnfamous1.chainsaw_man.client.keymapping.handler.AttackChainsawManHandler;
import me.Thelnfamous1.chainsaw_man.client.keymapping.handler.MorphChainsawManHandler;
import me.Thelnfamous1.chainsaw_man.client.keymapping.handler.SummonFoxDevilHandler;
import me.Thelnfamous1.chainsaw_man.client.network.CMClientPacketHandler;
import me.Thelnfamous1.chainsaw_man.client.renderer.ChainsawManRenderer;
import me.Thelnfamous1.chainsaw_man.client.renderer.ChainsawSweepRenderer;
import me.Thelnfamous1.chainsaw_man.client.renderer.FoxDevilRenderer;
import me.Thelnfamous1.chainsaw_man.common.CMMorphHelper;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ChainsawManModClient {

    private static CMOptions options;

    private static CMInput input;
    private static CMClientPacketHandler packetHandler;

    public static void init(IEventBus modEventBus){
        options = new CMOptions();
        input = new CMInput();
        input.registerInputHandler(options.keySummonFoxDevil, SummonFoxDevilHandler.INSTANCE);
        input.registerInputHandler(options.keyMorphChainsawMan, MorphChainsawManHandler.INSTANCE);
        input.registerInputHandler(options.keyAttackChainsawMan, AttackChainsawManHandler.INSTANCE);
        packetHandler = new CMClientPacketHandler();

        modEventBus.addListener((FMLClientSetupEvent event) -> {
            ChainsawManModClient.registerEntityRenderers();
            event.enqueueWork(ChainsawManModClient::registerKeyBindings);
        });
        modEventBus.addListener((ParticleFactoryRegisterEvent event) -> {
            Minecraft.getInstance().particleEngine.register(ChainsawManMod.CHAINSAW_SWEEP_PARTICLE.get(), SweepAttackParticle.Factory::new);
        });
        MinecraftForge.EVENT_BUS.addListener(ChainsawManModClient::onLeftClickInput);
    }

    private static void onLeftClickInput(InputEvent.ClickInputEvent event) {
        LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity(Minecraft.getInstance().player);
        if (event.isAttack() && activeMorphEntity != null && activeMorphEntity.getType() == ChainsawManMod.CHAINSAW_MAN.get()) {
            ChainsawMan chainsawMan = (ChainsawMan) activeMorphEntity;
            if (chainsawMan.isAttackAnimationInProgress()) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    private static void registerEntityRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(ChainsawManMod.CHAINSAW_MAN.get(), ChainsawManRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ChainsawManMod.FOX_DEVIL.get(), FoxDevilRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ChainsawManMod.CHAINSAW_SWEEP.get(), ChainsawSweepRenderer::new);
    }

    private static void registerKeyBindings(){
        ClientRegistry.registerKeyBinding(options.keySummonFoxDevil);
        ClientRegistry.registerKeyBinding(options.keyMorphChainsawMan);
        ClientRegistry.registerKeyBinding(options.keyAttackChainsawMan);
    }

    public static CMOptions getOptions() {
        return options;
    }

    public static CMInput getInput() {
        return input;
    }

    public static CMClientPacketHandler getPacketHandler() {
        return packetHandler;
    }
}
