package blazingtwist.itemcounts.mixin;

import blazingtwist.itemcounts.ItemCounts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphicsExtractor.class)
public abstract class DrawContextMixin {

	@Redirect(
			method = "itemCount(" +
					"Lnet/minecraft/client/gui/Font;" +
					"Lnet/minecraft/world/item/ItemStack;" +
					"I" +
					"I" +
					"Ljava/lang/String;" +
					")V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;" +
							"text(" +
							"Lnet/minecraft/client/gui/Font;" +
							"Ljava/lang/String;" +
							"I" +
							"I" +
							"I" +
							"Z" +
							")V"
			)
	)
	private void redirectDrawItemSlotText(
			GuiGraphicsExtractor instance, Font font, String text, int x, int y, int color, boolean shadow
	) {
		boolean isCalledFromHotbarRenderItem = ItemCounts.mixin_drawItemCalledFromRenderHotbarItem;
		if (ItemCounts.mixin_drawItemCalledFromRenderHotbarItem) {
			ItemCounts.mixin_drawItemCalledFromRenderHotbarItem = false;
		}

		if (!isCalledFromHotbarRenderItem || ItemCounts.getConfig().show_vanilla_count) {
			instance.text(font, text, x, y, color, shadow);
		}

	}

}
