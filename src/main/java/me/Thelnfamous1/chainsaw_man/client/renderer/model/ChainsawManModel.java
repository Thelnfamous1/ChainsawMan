package me.Thelnfamous1.chainsaw_man.client.renderer.model;

import me.Thelnfamous1.chainsaw_man.ChainsawManMod;
import me.Thelnfamous1.chainsaw_man.common.entity.ChainsawMan;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ChainsawManModel extends AnimatedGeoModel<ChainsawMan> {
	   
		@Override
		public ResourceLocation getAnimationFileLocation(ChainsawMan entity) {
			return new ResourceLocation(ChainsawManMod.MODID, "animations/chainsaw_man.animation.json");
		}

		@Override
		public ResourceLocation getModelLocation(ChainsawMan entity) {
			return new ResourceLocation(ChainsawManMod.MODID, "geo/chainsaw_man.geo.json");
		}

		@Override
		public ResourceLocation getTextureLocation(ChainsawMan entity) {
				return new ResourceLocation(ChainsawManMod.MODID, "textures/entity/chainsaw_man.png");
		}

		@Override
		public void setLivingAnimations(ChainsawMan entity, Integer uniqueID, AnimationEvent event) {
			super.setLivingAnimations(entity, uniqueID, event);
			IBone head = this.getAnimationProcessor().getBone("head");

			EntityModelData extraData = (EntityModelData) event.getExtraDataOfType(EntityModelData.class).get(0);
			if (extraData.headPitch != 0 || extraData.netHeadYaw != 0) {
				head.setRotationX(head.getRotationX() + (extraData.headPitch * ((float) Math.PI / 180F)));
				head.setRotationY(head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 180F)));
			}
		}
	}