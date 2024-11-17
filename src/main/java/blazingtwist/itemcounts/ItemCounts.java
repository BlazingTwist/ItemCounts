package blazingtwist.itemcounts;

import blazingtwist.itemcounts.config.AutoConfigKeybind;
import blazingtwist.itemcounts.config.ItemCountsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;

import java.lang.reflect.Field;
import java.util.Collections;

public class ItemCounts implements ClientModInitializer {
	public static final float FONT_HEIGHT = 8; // sitting here because mixin doesn't like it in 'InGameHudMixin'
	public static final float FONT_Y_OFFSET = 8;
	public static final float HOTBAR_X_OFFSET = 8;

	public static boolean mixin_drawItemCalledFromRenderHotbarItem;

	private static ConfigHolder<ItemCountsConfig> configHolder;

	public static ItemCountsConfig getConfig() {
		return configHolder.getConfig();
	}

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ItemCountsConfig.class, GsonConfigSerializer::new);
		registerAutoconfigTypes();

		configHolder = AutoConfig.getConfigHolder(ItemCountsConfig.class);
		registerKeybindListener();
	}

	private static void registerAutoconfigTypes() {
		GuiRegistry configRegistry = AutoConfig.getGuiRegistry(ItemCountsConfig.class);
		configRegistry.registerAnnotationProvider((i13n, field, config, defaults, guiProvider) -> {
			ConfigEntryBuilder entry = ConfigEntryBuilder.create();
			int key = getUnsafe(field, config, -1);
			int keyDef = getUnsafe(field, defaults, -1);

			return Collections.singletonList(entry
					.startKeyCodeField(new net.minecraft.text.TranslatableText(i13n), key > 0 ? InputUtil.fromKeyCode(key, -1) : InputUtil.UNKNOWN_KEY)
					.setDefaultValue(keyDef > 0 ? InputUtil.fromKeyCode(keyDef, -1) : InputUtil.UNKNOWN_KEY)
					.setKeySaveConsumer(saveKey -> setUnsafe(field, config, saveKey.getCode()))
					.build()
			);
		}, AutoConfigKeybind.class);
	}

	private static <T> T getUnsafe(Field field, Object obj, T fallback) {
		T result;
		try {
			//noinspection unchecked
			result = (T) field.get(obj);
		} catch (IllegalAccessException e) {
			System.err.println("failed to access field '" + field.getName() + "' on obj: " + obj);
			result = null;
		}
		return result == null ? fallback : result;
	}

	private static <T> void setUnsafe(Field field, Object obj, T value) {
		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			System.err.println("failed to set field '" + field.getName() + "' on obj: " + obj);
		}
	}

	private static void registerKeybindListener() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			long windowHandle = client.getWindow().getHandle();
			ItemCountsConfig config = configHolder.get();
			config.hotbar_relativeToHotbarConfig.handleKeys(windowHandle);
			config.mainHand_relativeToCrosshairConfig.handleKeys(windowHandle);
			config.mainHand_relativeToHotbarConfig.handleKeys(windowHandle);
			config.offHand_relativeToCrosshairConfig.handleKeys(windowHandle);
			config.offHand_relativeToHotbarConfig.handleKeys(windowHandle);
		});
	}
}
