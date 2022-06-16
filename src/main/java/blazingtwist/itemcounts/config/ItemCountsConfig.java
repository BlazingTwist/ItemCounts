package blazingtwist.itemcounts.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "itemcounts")
public class ItemCountsConfig implements ConfigData {
	@ConfigEntry.Category("mainHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig mainHand_relativeToHotbarConfig = new ItemRenderConfig(false, CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE, DurabilityDisplayOption.ALWAYS, IconDisplayOption.ALWAYS,
			0, -5, 0.75f);
	@ConfigEntry.Category("mainHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig mainHand_relativeToCrosshairConfig = new ItemRenderConfig(true, CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE, DurabilityDisplayOption.ALWAYS, IconDisplayOption.ALWAYS,
			-10, 10, 0.5f);

	@ConfigEntry.Category("offHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig offHand_relativeToHotbarConfig = new ItemRenderConfig(false, CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE, DurabilityDisplayOption.ALWAYS, IconDisplayOption.ALWAYS,
			0, -5, 0.75f);
	@ConfigEntry.Category("offHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig offHand_relativeToCrosshairConfig = new ItemRenderConfig(true, CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE, DurabilityDisplayOption.ALWAYS, IconDisplayOption.ALWAYS,
			10, 10, 0.5f);

	@ConfigEntry.Category("hotbar")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig hotbar_relativeToHotbarConfig = new ItemRenderConfig(true, CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE, DurabilityDisplayOption.ALWAYS, IconDisplayOption.NEVER,
			0, -5, 0.75f);

	public static class ItemRenderConfig {
		@AutoConfigConstructor
		public ItemRenderConfig() {
		}

		public ItemRenderConfig(boolean enabled, CountDisplayOption countOption,
								DurabilityItemOption durabilityFilter, DurabilityDisplayOption durabilityOption,
								IconDisplayOption iconOption,
								int xOffset, int yOffset, float textScale) {
			this.enabled = enabled;
			this.countOption = countOption;
			this.durabilityFilter = durabilityFilter;
			this.durabilityOption = durabilityOption;
			this.iconOption = iconOption;
			offset = new HudOffset(xOffset, yOffset, textScale);
		}

		public boolean enabled = false;
		public CountDisplayOption countOption = CountDisplayOption.NEVER;
		public DurabilityItemOption durabilityFilter = DurabilityItemOption.NONE;
		public DurabilityDisplayOption durabilityOption = DurabilityDisplayOption.NEVER;
		public IconDisplayOption iconOption = IconDisplayOption.NEVER;
		@ConfigEntry.Gui.CollapsibleObject()
		public HudOffset offset = new HudOffset(0, -5, 0.75f);
	}

	public static class HudOffset {
		@AutoConfigConstructor
		public HudOffset() {
		}

		public HudOffset(int x, int y, float textScale) {
			this.x = x;
			this.y = y;
			this.textScale = textScale;
		}

		public int x = 0;
		public int y = 0;
		public float textScale = 1f;
	}
}
