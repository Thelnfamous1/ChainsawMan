package me.Thelnfamous1.chainsaw_man;

import me.Thelnfamous1.chainsaw_man.client.ChainsawManModClient;
import me.Thelnfamous1.chainsaw_man.client.keymapping.CMOptions;
import me.Thelnfamous1.chainsaw_man.common.CMMorphHelper;
import me.Thelnfamous1.chainsaw_man.common.ability.CMSpecialAttack;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawManAttackType;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawSweep;
import me.Thelnfamous1.chainsaw_man.common.entity.FoxDevil;
import me.Thelnfamous1.chainsaw_man.common.network.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Optional;

@Mod(ChainsawManMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChainsawManMod {
    public static final String MODID = "chainsaw_man";
    public static final Logger LOGGER = LogManager.getLogger();


    private static final GameRules.RuleKey<GameRules.BooleanValue> RULE_CHAINSAW_MAN = GameRules.register("chainsawman", GameRules.Category.PLAYER, createBooleanGameRule(false));
    private static final GameRules.RuleKey<GameRules.BooleanValue> RULE_AKI_FOX = GameRules.register("akifox", GameRules.Category.PLAYER, createBooleanGameRule(false));

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<FoxDevil>> FOX_DEVIL = registerEntityType("fox_devil",
            EntityType.Builder.<FoxDevil>of(FoxDevil::new, EntityClassification.MISC)
            .sized(10.0F, 10.0F)
            .clientTrackingRange(6)
            .updateInterval(2));

    public static final RegistryObject<EntityType<ChainsawMan>> CHAINSAW_MAN = registerEntityType("chainsaw_man",
            EntityType.Builder.of(ChainsawMan::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.8F + 0.5F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ChainsawSweep>> CHAINSAW_SWEEP = registerEntityType("chainsaw_sweep",
            EntityType.Builder.<ChainsawSweep>of(ChainsawSweep::new, EntityClassification.MISC)
                    .fireImmune()
                    .sized(2.0F, 2.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<BasicParticleType> CHAINSAW_SWEEP_PARTICLE = PARTICLE_TYPES.register("chainsaw_sweep", () -> new BasicParticleType(true));

    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(MODID, "network"))
            .clientAcceptedVersions("1"::equals)
            .serverAcceptedVersions("1"::equals)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    private static int PACKET_COUNTER = 0;

    public ChainsawManMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        if(FMLEnvironment.dist == Dist.CLIENT){
            ChainsawManModClient.init(modEventBus);
        }
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerAttackEntity);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLeftClickEmpty);
    }

    // only called on client
    private void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity(event.getPlayer());
        if(activeMorphEntity != null && activeMorphEntity.getType() == CHAINSAW_MAN.get()){
            ChainsawMan chainsawMan = (ChainsawMan) activeMorphEntity;
            ChainsawManAttackType attackType = ChainsawManAttackType.byMainArm(event.getPlayer().getMainArm());
            if(!chainsawMan.isAttackAnimationInProgress() && event.getPlayer().level.isClientSide){
                NETWORK_CHANNEL.sendToServer(new ServerboundSpecialAttackPacket(CMSpecialAttack.byAttackType(attackType), true));
            }
        }
    }

    // called on client, then server
    private void onPlayerAttackEntity(AttackEntityEvent event) {
        LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity(event.getPlayer());
        if(activeMorphEntity != null && activeMorphEntity.getType() == CHAINSAW_MAN.get()){
            ChainsawMan chainsawMan = (ChainsawMan) activeMorphEntity;
            if(chainsawMan.isAttackAnimationInProgress()){
                event.setCanceled(true);
            } else {
                ChainsawManAttackType attackType = ChainsawManAttackType.byMainArm(event.getPlayer().getMainArm());
                if(!event.getPlayer().level.isClientSide && chainsawMan.startAttack(attackType)){
                    NETWORK_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getPlayer), new ClientboundStartSpecialAttackPacket(event.getPlayer(), CMSpecialAttack.byAttackType(attackType)));
                }
            }
        }
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () ->
                builder
                        .build(MODID + ":" + name));
    }

    private static GameRules.RuleType<GameRules.BooleanValue> createBooleanGameRule(boolean defaultValue) {
        Method create = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "func_223568_b", boolean.class);
        try {
            return (GameRules.RuleType<GameRules.BooleanValue>)create.invoke(null, defaultValue);
        } catch (Exception e) {
            LOGGER.error("Failed to create game rule!");
            return null;
        }
    }

    public static GameRules.RuleKey<GameRules.BooleanValue> getRuleChainsawMan() {
        return RULE_CHAINSAW_MAN;
    }

    public static GameRules.RuleKey<GameRules.BooleanValue> getRuleAkiFox() {
        return RULE_AKI_FOX;
    }

    private void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.CLIENT
                && event.phase == TickEvent.Phase.END
                && !event.player.removed
                && event.player.getId() > 0)
        {
            LivingEntity activeMorphEntity = CMMorphHelper.getActiveMorphEntity(event.player);
            if(activeMorphEntity != null && activeMorphEntity.getType() == CHAINSAW_MAN.get()){
                ((ChainsawMan)activeMorphEntity).tickAnimatedAttack();
            }
        }
    }

    @SubscribeEvent
    static void onGatherData(GatherDataEvent event){
        event.getGenerator().addProvider(new LanguageProvider(event.getGenerator(), MODID, "en_us") {
            @Override
            protected void addTranslations() {
                this.addEntityType(FOX_DEVIL, "Fox Devil");
                this.addEntityType(CHAINSAW_MAN, "Chainsaw Man");
                this.add(CMOptions.SUMMON_FOX_DEVIL, "Summon Fox Devil");
                this.add(CMOptions.MORPH_CHAINSAW_MAN, "Morph into / Unmorph from Chainsaw Man");
                this.add(CMOptions.ATTACK_CHAINSAW_MAN, "Attack as Chainsaw Man");
                this.add(CMOptions.CATEGORY, "Chainsaw Man");
                this.add("gamerule.chainsawman", "Allow Chainsaw Man Morphing");
                this.add("gamerule.akifox", "Allow Fox Devil Summoning");
            }
        });
    }

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
        // do something when the setup is run on both client and server
        event.enqueueWork(ChainsawManMod::setupNetwork);
    }

    @SubscribeEvent
    static void onEntityAttributeCreation(EntityAttributeCreationEvent event){
        event.put(CHAINSAW_MAN.get(), ChainsawMan.createAttributes().build());
    }

    public static void setupNetwork() {
        NETWORK_CHANNEL.registerMessage(
                incrementAndGetPacketCounter(),
                ServerboundAbilityPacket.class,
                ServerboundAbilityPacket::encode,
                ServerboundAbilityPacket::decode,
                ServerboundAbilityPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        NETWORK_CHANNEL.registerMessage(
                incrementAndGetPacketCounter(),
                ServerboundMorphPacket.class,
                ServerboundMorphPacket::encode,
                ServerboundMorphPacket::decode,
                ServerboundMorphPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        NETWORK_CHANNEL.registerMessage(
                incrementAndGetPacketCounter(),
                ServerboundSpecialAttackPacket.class,
                ServerboundSpecialAttackPacket::encode,
                ServerboundSpecialAttackPacket::decode,
                ServerboundSpecialAttackPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        NETWORK_CHANNEL.registerMessage(
                incrementAndGetPacketCounter(),
                ClientboundStartSpecialAttackPacket.class,
                ClientboundStartSpecialAttackPacket::encode,
                ClientboundStartSpecialAttackPacket::decode,
                ClientboundStartSpecialAttackPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        NETWORK_CHANNEL.registerMessage(
                incrementAndGetPacketCounter(),
                ClientboundStopSpecialAttackPacket.class,
                ClientboundStopSpecialAttackPacket::encode,
                ClientboundStopSpecialAttackPacket::decode,
                ClientboundStopSpecialAttackPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static int incrementAndGetPacketCounter() {
        return PACKET_COUNTER++;
    }
}
