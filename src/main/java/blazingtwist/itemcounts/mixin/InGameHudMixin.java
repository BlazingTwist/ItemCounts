package blazingtwist.itemcounts.mixin;

import blazingtwist.itemcounts.ItemCounts;
import blazingtwist.itemcounts.config.ItemCountsConfig;
import blazingtwist.itemcounts.util.ColorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private void renderItemOverlay(GuiGraphics context, ItemCountsConfig.ItemRenderConfig config, boolean onHotbar,
								   Player player, ItemStack stack, int x, int y, int seed) {
		if (!config.isEnabled()) {
			return;
		}
		if (!onHotbar) {
			x = context.guiWidth() / 2;
			y = context.guiHeight() / 2;
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
	private boolean shouldRenderItem(ItemCountsConfig.ItemRenderConfig config, Player player, ItemStack stack) {
		if (stack.isDamageableItem() && config.durabilityFilter.showDurabilityInsteadOfItemCount(stack)) {
			return config.durabilityOption.shouldShowDurability(stack);
		} else {
			return config.countOption.shouldShowCount(player, stack);
		}
	}

	@Unique
	private void renderItemText(GuiGraphics context, ItemCountsConfig.ItemRenderConfig config, boolean onHotbar,
								Player player, ItemStack stack, int x, int y) {
		final String text;
		final int color;
		if (stack.isDamageableItem() && config.durabilityFilter.showDurabilityInsteadOfItemCount(stack)) {
			int maxDamage = stack.getMaxDamage();
			int currentDamage = stack.getDamageValue();
			text = "" + (maxDamage - currentDamage);
			if (config.colors.enableCustomColors) {
				float damageFraction = ((float) currentDamage) / maxDamage;
				color = ColorHelper.lerpColor(damageFraction, config.colors.colorDurabilityHigh, config.colors.colorDurabilityLow);
			} else {
				color = stack.getBarColor();
			}
		} else {
			text = "" + ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack);
			color = config.colors.enableCustomColors ? config.colors.colorItemCount : -1;
		}

		renderTextAt(context, config, text, color, x, y, onHotbar);
	}

	@Unique
	private void renderTextAt(GuiGraphics context, ItemCountsConfig.ItemRenderConfig config,
							  String text, int color, int x, int y, boolean isOnHotbar) {
		float scaleFactor = config.offset.textScale;
		Matrix3x2fStack matrices = context.pose();
		matrices.pushMatrix();
		matrices.translate(0, ItemCounts.FONT_Y_OFFSET * scaleFactor);
		if (isOnHotbar) {
			matrices.translate(ItemCounts.HOTBAR_X_OFFSET * scaleFactor, 0);
		}
		matrices.scale(scaleFactor, scaleFactor);

		context.drawString(
				minecraft.font,
				text,
				(int) (config.offset.anchor.applyAnchorOffset(x / scaleFactor, text, minecraft.font)),
				(int) ((y / scaleFactor) - (ItemCounts.FONT_HEIGHT / 2)),
				color >= 0 ? (0xff000000 | color) : -1,
				true
		);

		matrices.popMatrix();
	}

	@Unique
	private void renderItemAt(GuiGraphics context, ItemStack item, int x, int y, float scaleFactor, boolean isOnHotbar, Player player, int seed) {
		Matrix3x2fStack contextMatrices = context.pose();
		contextMatrices.pushMatrix();
		contextMatrices.scale(scaleFactor, scaleFactor);

		if (isOnHotbar) {
			contextMatrices.translate(ItemCounts.HOTBAR_X_OFFSET, 0);
		}
		int scaledX = ((int) (x / scaleFactor)) - 8; // -8 to offset the reapplied offset in 'drawItem'...
		int scaledY = ((int) (y / scaleFactor)) - 8;
		context.renderItem(player, item, scaledX, scaledY, seed);

		contextMatrices.popMatrix();
	}

	@Inject(method = "renderSlot(" +
			"Lnet/minecraft/client/gui/GuiGraphics;" +
			"I" +
			"I" +
			"Lnet/minecraft/client/DeltaTracker;" +
			"Lnet/minecraft/world/entity/player/Player;" +
			"Lnet/minecraft/world/item/ItemStack;" +
			"I" +
			")V", at = @At("TAIL"), order = 999)
	public void onRenderHotbarItem(GuiGraphics context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo info) {
		if (stack.isEmpty()) {
			return;
		}

		ItemCountsConfig config = ItemCounts.getConfig();

		if (player.getMainHandItem() == stack) {
			renderItemOverlay(context, config.mainHand_relativeToCrosshairConfig, false, player, stack, x, y, seed);
			renderItemOverlay(context, config.mainHand_relativeToHotbarConfig, true, player, stack, x, y, seed);
		}

		if (player.getOffhandItem() == stack) {
			renderItemOverlay(context, config.offHand_relativeToCrosshairConfig, false, player, stack, x, y, seed);
			renderItemOverlay(context, config.offHand_relativeToHotbarConfig, true, player, stack, x, y, seed);
		}

		renderItemOverlay(context, config.hotbar_relativeToHotbarConfig, true, player, stack, x, y, seed);
	}

	@Inject(
			method = "renderSlot(" +
					"Lnet/minecraft/client/gui/GuiGraphics;" +
					"I" +
					"I" +
					"Lnet/minecraft/client/DeltaTracker;" +
					"Lnet/minecraft/world/entity/player/Player;" +
					"Lnet/minecraft/world/item/ItemStack;" +
					"I" +
					")V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;" +
							"renderItemDecorations(" +
							"Lnet/minecraft/client/gui/Font;" +
							"Lnet/minecraft/world/item/ItemStack;" +
							"I" +
							"I" +
							")V",
					shift = At.Shift.BEFORE
			)
	)
	private void onBefore_drawHotbarItem_call_drawItemInSlot(
			GuiGraphics context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo info
	) {
		ItemCounts.mixin_drawItemCalledFromRenderHotbarItem = true;
	}

}
