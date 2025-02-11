package software.bernie.geckolib3.renderers.geo;

import java.awt.Color;
import java.util.Collections;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatableSingleton;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class GeoItemRenderer<T extends Item & IAnimatableSingleton<ItemStack>> extends BlockEntityWithoutLevelRenderer implements IGeoRenderer<T> {

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance()
				.getEntityModels(), modelProvider);
	}

	public GeoItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet,
			AnimatedGeoModel<T> modelProvider) {
		super(dispatcher, modelSet);
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return modelProvider;
	}

	// fixes the item lighting
	@Override
	public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack,
			MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		if (p_239207_2_ == ItemTransforms.TransformType.GUI) {
			matrixStack.pushPose();
			MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers()
					.bufferSource();
			Lighting.setupForFlatItems();
			this.render((T) itemStack.getItem(), matrixStack, bufferIn, combinedLightIn, itemStack);
			irendertypebuffer$impl.endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			matrixStack.popPose();
		} else {
			this.render((T) itemStack.getItem(), matrixStack, bufferIn, combinedLightIn, itemStack);
		}
	}

	public void render(T animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn,
			ItemStack itemStack) {
		this.currentItemStack = itemStack;
		AnimatingModel model = modelProvider.getModel(animatable);
		AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0, Minecraft.getInstance()
				.getFrameTime(), false, Collections.singletonList(itemStack));
		AnimationData data = animatable.getAnimationData(itemStack);
		modelProvider.setLivingAnimations(animatable, data, itemEvent);
		stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn, getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

}
