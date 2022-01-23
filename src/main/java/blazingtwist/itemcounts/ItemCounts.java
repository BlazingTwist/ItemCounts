package blazingtwist.itemcounts;

import blazingtwist.itemcounts.config.ItemCountsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class ItemCounts implements ModInitializer {
	public static final float FONT_HEIGHT = 8; // sitting here because mixin doesn't like it in 'InGameHudMixin'
	public static final float FONT_Y_OFFSET = 8;
	public static final float HOTBAR_X_OFFSET = 8;

	private static ConfigHolder<ItemCountsConfig> configHolder;

	public static ItemCountsConfig getConfig(){
		return configHolder.getConfig();
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		AutoConfig.register(ItemCountsConfig.class, GsonConfigSerializer::new);

		configHolder = AutoConfig.getConfigHolder(ItemCountsConfig.class);
	}
}
