package blazingtwist.itemcounts.mixin;

import blazingtwist.itemcounts.ItemCounts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

	@Redirect(
			method = "drawStackCount(" +
					"Lnet/minecraft/client/font/TextRenderer;" +
					"Lnet/minecraft/item/ItemStack;" +
					"I" +
					"I" +
					"Ljava/lang/String;" +
					")V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;" +
							"drawText(" +
							"Lnet/minecraft/client/font/TextRenderer;" +
							"Ljava/lang/String;" +
							"I" +
							"I" +
							"I" +
							"Z" +
							")I"
			)
	)
	private int redirectDrawItemSlotText(
			DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow
	) {
		boolean isCalledFromHotbarRenderItem = ItemCounts.mixin_drawItemCalledFromRenderHotbarItem;
		if (ItemCounts.mixin_drawItemCalledFromRenderHotbarItem) {
			ItemCounts.mixin_drawItemCalledFromRenderHotbarItem = false;
		}

		if (isCalledFromHotbarRenderItem && !ItemCounts.getConfig().show_vanilla_count) {
			return 0;
		}

		return instance.drawText(textRenderer, text, x, y, color, shadow);
	}

}
