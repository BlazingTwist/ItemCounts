package blazingtwist.itemcounts.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "itemcounts")
public class ItemCountsConfig implements ConfigData {
	@ConfigEntry.Category("category.itemcounts")
	public CountDisplayOption showActiveItemCount = CountDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	public DurabilityItemOption showActiveItemDurabilityFilter = DurabilityItemOption.GOLD_DIAMOND_NETHERITE;
	@ConfigEntry.Category("category.itemcounts")
	public DurabilityDisplayOption showActiveItemDurability = DurabilityDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	public float activeItemCountTextScale = 0.5f;
	@ConfigEntry.Category("category.itemcounts")
	public IconDisplayOption showActiveItemIcon = IconDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	@ConfigEntry.Gui.CollapsibleObject
	public HudOffset activeItemCountOffset = new HudOffset(-10, 10);

	@ConfigEntry.Category("category.itemcounts")
	public CountDisplayOption showOffhandItemCount = CountDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	public DurabilityItemOption showOffhandItemDurabilityFilter = DurabilityItemOption.GOLD_DIAMOND_NETHERITE;
	@ConfigEntry.Category("category.itemcounts")
	public DurabilityDisplayOption showOffhandItemDurability = DurabilityDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	public float offhandItemCountTextScale = 0.5f;
	@ConfigEntry.Category("category.itemcounts")
	public IconDisplayOption showOffhandItemIcon = IconDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	@ConfigEntry.Gui.CollapsibleObject
	public HudOffset offhandItemCountOffset = new HudOffset(10, 10);


	@ConfigEntry.Category("category.itemcounts")
	public CountDisplayOption showOnHotbar = CountDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	public DurabilityItemOption showHotbarItemDurabilityFilter = DurabilityItemOption.GOLD_DIAMOND_NETHERITE;
	@ConfigEntry.Category("category.itemcounts")
	public DurabilityDisplayOption showHotbarItemDurability = DurabilityDisplayOption.ALWAYS;
	@ConfigEntry.Category("category.itemcounts")
	public float hotbarItemCountTextScale = 0.75f;
	@ConfigEntry.Category("category.itemcounts")
	public IconDisplayOption showHotbarItemIcon = IconDisplayOption.NEVER;
	@ConfigEntry.Category("category.itemcounts")
	@ConfigEntry.Gui.CollapsibleObject
	public HudOffset hotbarItemCountOffset = new HudOffset(0, -5);

	public static class HudOffset {
		public HudOffset() {
		}

		public HudOffset(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int x = 0;
		public int y = 0;
	}
}
