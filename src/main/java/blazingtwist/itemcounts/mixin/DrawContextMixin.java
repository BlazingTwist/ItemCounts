package blazingtwist.itemcounts.mixin;

import blazingtwist.itemcounts.ItemCounts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class DrawContextMixin {

	@Redirect(
			method = "renderGuiItemOverlay(" +
					"Lnet/minecraft/client/font/TextRenderer;" +
					"Lnet/minecraft/item/ItemStack;" +
					"I" +
					"I" +
					")V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/ItemRenderer;" +
							"renderGuiItemOverlay(" +
							"Lnet/minecraft/client/font/TextRenderer;" +
							"Lnet/minecraft/item/ItemStack;" +
							"I" +
							"I" +
							"Ljava/lang/String;" +
							")V",
					remap = false)
	)
	private void redirectDrawItemSlotText(
			ItemRenderer instance, TextRenderer textRenderer, ItemStack stack, int x, int y, String textOverride
	) {
		boolean isCalledFromHotbarRenderItem = ItemCounts.mixin_drawItemCalledFromRenderHotbarItem;
		if (ItemCounts.mixin_drawItemCalledFromRenderHotbarItem) {
			ItemCounts.mixin_drawItemCalledFromRenderHotbarItem = false;
		}

		if (isCalledFromHotbarRenderItem && !ItemCounts.getConfig().show_vanilla_count) {
			instance.renderGuiItemOverlay(textRenderer, stack, x, y, "");
		} else {
			instance.renderGuiItemOverlay(textRenderer, stack, x, y, textOverride);
		}
	}

}
