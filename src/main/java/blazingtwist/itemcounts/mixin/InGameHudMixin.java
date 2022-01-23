package blazingtwist.itemcounts.mixin;

import blazingtwist.itemcounts.ItemCounts;
import blazingtwist.itemcounts.config.ItemCountsConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private ItemRenderer itemRenderer;

	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	@Shadow
	private PlayerEntity getCameraPlayer() {
		throw new NotImplementedException("Stub method called!");
	}

	private int getTotalItemCount(PlayerEntity player, ItemStack stack) {
		return Stream.concat(player.inventory.main.stream(), player.inventory.offHand.stream())
				.filter(other -> other.isItemEqual(stack))
				.mapToInt(ItemStack::getCount)
				.sum();
	}

	private void renderActiveItemCount(ItemCountsConfig config, PlayerEntity player, ItemStack stack) {
		if (!config.showActiveItemCount || stack.isEmpty()) {
			return;
		}

		int x = scaledWidth / 2 + config.activeItemCountOffset.x;
		int y = scaledHeight / 2 + config.activeItemCountOffset.y;
		renderTextAt("" + getTotalItemCount(player, stack), x, y, config.activeItemCountTextScale, false);
		if (config.showActiveItemIcon) {
			renderItemAt(stack, x, y, config.activeItemCountTextScale, false);
		}
	}

	private void renderOffhandItemCount(ItemCountsConfig config, PlayerEntity player, ItemStack stack) {
		if (!config.showOffhandItemCount || stack.isEmpty()) {
			return;
		}

		int x = scaledWidth / 2 + config.offhandItemCountOffset.x;
		int y = scaledHeight / 2 + config.offhandItemCountOffset.y;
		renderTextAt("" + getTotalItemCount(player, stack), x, y, config.offhandItemCountTextScale, false);
		if (config.showOffhandItemIcon) {
			renderItemAt(stack, x, y, config.offhandItemCountTextScale, false);
		}
	}

	private void renderHotbarItemCount(ItemCountsConfig config, PlayerEntity player, int x, int y, ItemStack stack) {
		if (!config.showOnHotbar || stack.isEmpty()) {
			return;
		}

		x += config.hotbarItemCountOffset.x;
		y += config.hotbarItemCountOffset.y;
		renderTextAt("" + getTotalItemCount(player, stack), x, y, config.hotbarItemCountTextScale, true);
		if (config.showHotbarItemIcon) {
			renderItemAt(stack, x, y, config.hotbarItemCountTextScale, true);
		}
	}

	private void renderTextAt(String text, int x, int y, float scaleFactor, boolean isOnHotbar) {
		TextRenderer renderer = client.textRenderer;
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.translate(0, ItemCounts.FONT_Y_OFFSET * scaleFactor, 250);
		if (isOnHotbar) {
			matrixStack.translate(ItemCounts.HOTBAR_X_OFFSET * scaleFactor, 0, 0);
		}
		matrixStack.scale(scaleFactor, scaleFactor, 1);
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		renderer.draw(
				text,
				(x / scaleFactor) - (renderer.getWidth(text) / 2f),
				(y / scaleFactor) - (ItemCounts.FONT_HEIGHT / 2),
				16777215,
				true,
				matrixStack.peek().getModel(),
				immediate,
				false,
				0,
				15728880);
		immediate.draw();
	}

	private void renderItemAt(ItemStack item, int x, int y, float scaleFactor, boolean isOnHotbar) {
		TextureManager textureManager;
		try {
			textureManager = (TextureManager) itemRenderer.getClass()
					.getDeclaredField("textureManager")
					.get(itemRenderer);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			System.err.println("Failed to gather textureManager from ItemRenderer!");
			e.printStackTrace();
			return;
		}
		RenderSystem.pushMatrix();
		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.enableRescaleNormal();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.translatef(x, y, 100.0F);
		if (isOnHotbar) {
			RenderSystem.translatef(ItemCounts.HOTBAR_X_OFFSET * scaleFactor, 0, 0);
		}
		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
		RenderSystem.scalef(16.0F, 16.0F, 16.0F);
		MatrixStack matrixStack2 = new MatrixStack();
		matrixStack2.scale(scaleFactor, scaleFactor, scaleFactor);

		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		BakedModel model = itemRenderer.getHeldItemModel(item, null, null);
		boolean bl = !model.isSideLit();
		if (bl) {
			DiffuseLighting.disableGuiDepthLighting();
		}

		itemRenderer.renderItem(item, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
		immediate.draw();
		RenderSystem.enableDepthTest();
		if (bl) {
			DiffuseLighting.enableGuiDepthLighting();
		}

		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
	}

	@Inject(method = "renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V", at = @At("HEAD"))
	public void onRenderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo info) {
		PlayerEntity cameraPlayer = getCameraPlayer();
		if (cameraPlayer == null) {
			return;
		}

		ItemCountsConfig config = ItemCounts.getConfig();
		renderActiveItemCount(config, cameraPlayer, cameraPlayer.getMainHandStack());
		renderOffhandItemCount(config, cameraPlayer, cameraPlayer.getOffHandStack());
	}

	@Inject(method = "renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
	public void onRenderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, CallbackInfo info) {
		if (stack.isEmpty()) {
			return;
		}

		ItemCountsConfig config = ItemCounts.getConfig();
		renderHotbarItemCount(config, player, x, y, stack);
	}
}
