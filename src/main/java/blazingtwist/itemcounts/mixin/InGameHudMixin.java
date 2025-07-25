package blazingtwist.itemcounts.mixin;

import blazingtwist.itemcounts.ItemCounts;
import blazingtwist.itemcounts.config.ItemCountsConfig;
import blazingtwist.itemcounts.util.ColorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private void renderItemOverlay(DrawContext context, ItemCountsConfig.ItemRenderConfig config, boolean onHotbar,
								   PlayerEntity player, ItemStack stack, int x, int y, int seed) {
		if (!config.isEnabled()) {
			return;
		}
		if (!onHotbar) {
			x = context.getScaledWindowWidth() / 2;
			y = context.getScaledWindowHeight() / 2;
		}
		x += config.offset.x;
		y += config.offset.y;

		boolean doRenderText = shouldRenderItem(config, player, stack);
		if (config.iconOption.shouldShowIcon(doRenderText)) {
			renderItemAt(context, stack, x, y, config.offset.textScale, onHotbar, player, seed);
		}
		if (doRenderText) {
			renderItemText(context, config, onHotbar, player, stack, x, y);
		}
	}

	@Unique
	private boolean shouldRenderItem(ItemCountsConfig.ItemRenderConfig config, PlayerEntity player, ItemStack stack) {
		if (stack.isDamageable() && config.durabilityFilter.showDurabilityInsteadOfItemCount(stack)) {
			return config.durabilityOption.shouldShowDurability(stack);
		} else {
			return config.countOption.shouldShowCount(player, stack);
		}
	}

	@Unique
	private void renderItemText(DrawContext context, ItemCountsConfig.ItemRenderConfig config, boolean onHotbar,
								PlayerEntity player, ItemStack stack, int x, int y) {
		final String text;
		final int color;
		if (stack.isDamageable() && config.durabilityFilter.showDurabilityInsteadOfItemCount(stack)) {
			int maxDamage = stack.getMaxDamage();
			int currentDamage = stack.getDamage();
			text = "" + (maxDamage - currentDamage);
			if (config.colors.enableCustomColors) {
				float damageFraction = ((float) currentDamage) / maxDamage;
				color = ColorHelper.lerpColor(damageFraction, config.colors.colorDurabilityHigh, config.colors.colorDurabilityLow);
			} else {
				color = stack.getItemBarColor();
			}
		} else {
			text = "" + ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack);
			color = config.colors.enableCustomColors ? config.colors.colorItemCount : -1;
		}

		renderTextAt(context, config, text, color, x, y, onHotbar);
	}

	@Unique
	private void renderTextAt(DrawContext context, ItemCountsConfig.ItemRenderConfig config,
							  String text, int color, int x, int y, boolean isOnHotbar) {
		float scaleFactor = config.offset.textScale;
		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(0, ItemCounts.FONT_Y_OFFSET * scaleFactor);
		if (isOnHotbar) {
			matrices.translate(ItemCounts.HOTBAR_X_OFFSET * scaleFactor, 0);
		}
		matrices.scale(scaleFactor, scaleFactor);

		context.drawText(
				client.textRenderer,
				text,
				(int) (config.offset.anchor.applyAnchorOffset(x / scaleFactor, text, client.textRenderer)),
				(int) ((y / scaleFactor) - (ItemCounts.FONT_HEIGHT / 2)),
				color >= 0 ? (0xff000000 | color) : -1,
				true
		);

		matrices.popMatrix();
	}

	@Unique
	private void renderItemAt(DrawContext context, ItemStack item, int x, int y, float scaleFactor, boolean isOnHotbar, PlayerEntity player, int seed) {
		Matrix3x2fStack contextMatrices = context.getMatrices();
		contextMatrices.pushMatrix();
		contextMatrices.scale(scaleFactor, scaleFactor);

		if (isOnHotbar) {
			contextMatrices.translate(ItemCounts.HOTBAR_X_OFFSET, 0);
		}
		int scaledX = ((int) (x / scaleFactor)) - 8; // -8 to offset the reapplied offset in 'drawItem'...
		int scaledY = ((int) (y / scaleFactor)) - 8;
		context.drawItem(player, item, scaledX, scaledY, seed);

		contextMatrices.popMatrix();
	}

	@Inject(method = "renderHotbarItem(" +
			"Lnet/minecraft/client/gui/DrawContext;" +
			"I" +
			"I" +
			"Lnet/minecraft/client/render/RenderTickCounter;" +
			"Lnet/minecraft/entity/player/PlayerEntity;" +
			"Lnet/minecraft/item/ItemStack;" +
			"I" +
			")V", at = @At("TAIL"), order = 999)
	public void onRenderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo info) {
		if (stack.isEmpty()) {
			return;
		}

		ItemCountsConfig config = ItemCounts.getConfig();

		if (player.getMainHandStack() == stack) {
			renderItemOverlay(context, config.mainHand_relativeToCrosshairConfig, false, player, stack, x, y, seed);
			renderItemOverlay(context, config.mainHand_relativeToHotbarConfig, true, player, stack, x, y, seed);
		}

		if (player.getOffHandStack() == stack) {
			renderItemOverlay(context, config.offHand_relativeToCrosshairConfig, false, player, stack, x, y, seed);
			renderItemOverlay(context, config.offHand_relativeToHotbarConfig, true, player, stack, x, y, seed);
		}

		renderItemOverlay(context, config.hotbar_relativeToHotbarConfig, true, player, stack, x, y, seed);
	}

	@Inject(
			method = "renderHotbarItem(" +
					"Lnet/minecraft/client/gui/DrawContext;" +
					"I" +
					"I" +
					"Lnet/minecraft/client/render/RenderTickCounter;" +
					"Lnet/minecraft/entity/player/PlayerEntity;" +
					"Lnet/minecraft/item/ItemStack;" +
					"I" +
					")V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;" +
							"drawStackOverlay(" +
							"Lnet/minecraft/client/font/TextRenderer;" +
							"Lnet/minecraft/item/ItemStack;" +
							"I" +
							"I" +
							")V",
					shift = At.Shift.BEFORE
			)
	)
	private void onBefore_drawHotbarItem_call_drawItemInSlot(
			DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo info
	) {
		ItemCounts.mixin_drawItemCalledFromRenderHotbarItem = true;
	}

}
