package blazingtwist.itemcounts.config;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Config(name = "itemcounts")
public class ItemCountsConfig implements ConfigData {
	@ConfigEntry.Category("mainHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig mainHand_relativeToHotbarConfig = new ItemRenderConfig(
			false,
			CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE,
			DurabilityDisplayOption.ALWAYS,
			IconDisplayOption.ALWAYS,
			0,
			-5,
			0.75f
	);
	@ConfigEntry.Category("mainHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig mainHand_relativeToCrosshairConfig = new ItemRenderConfig(
			true,
			CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE,
			DurabilityDisplayOption.ALWAYS,
			IconDisplayOption.ALWAYS,
			-10,
			10,
			0.5f
	);

	@ConfigEntry.Category("offHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig offHand_relativeToHotbarConfig = new ItemRenderConfig(
			false,
			CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE,
			DurabilityDisplayOption.ALWAYS,
			IconDisplayOption.ALWAYS,
			0,
			-5,
			0.75f
	);
	@ConfigEntry.Category("offHand")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig offHand_relativeToCrosshairConfig = new ItemRenderConfig(
			true,
			CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE,
			DurabilityDisplayOption.ALWAYS,
			IconDisplayOption.ALWAYS,
			10,
			10,
			0.5f
	);

	@ConfigEntry.Category("hotbar")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemRenderConfig hotbar_relativeToHotbarConfig = new ItemRenderConfig(
			true,
			CountDisplayOption.ALWAYS,
			DurabilityItemOption.GOLD_DIAMOND_NETHERITE,
			DurabilityDisplayOption.ALWAYS,
			IconDisplayOption.NEVER,
			0,
			-5,
			0.75f
	);

	@ConfigEntry.Category("rules")
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemCountSeparationRules item_count_rules = new ItemCountSeparationRules(
			false,
			false,
			false
	);

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
		public TextAnchorOption anchor = TextAnchorOption.CENTER;
	}

	public static class ItemCountSeparationRules {
		@AutoConfigConstructor
		public ItemCountSeparationRules() {
		}

		public ItemCountSeparationRules(boolean separateMetadata, boolean separateName, boolean separateDurability) {
			this.separateNbtData = separateMetadata;
			this.separateName = separateName;
			this.separateDurability = separateDurability;
		}

		public boolean separateNbtData = false;
		public boolean separateName = false;
		public boolean separateDurability = false;

		public int getTotalItemCount(PlayerEntity player, ItemStack stack) {
			return Stream.concat(player.getInventory().main.stream(), player.getInventory().offHand.stream())
					.filter(other -> mergeStackCounts(stack, other))
					.mapToInt(ItemStack::getCount)
					.sum();
		}

		public int getHotbarItemCount(PlayerEntity player, ItemStack stack) {
			Stream<ItemStack> hotbarStacks = IntStream.range(0, 9)
					.mapToObj(i -> player.getInventory().main.get(i));

			return Stream.concat(hotbarStacks, player.getInventory().offHand.stream())
					.filter(other -> mergeStackCounts(stack, other))
					.mapToInt(ItemStack::getCount)
					.sum();
		}

		private boolean mergeStackCounts(ItemStack a, ItemStack b) {
			return b.isItemEqual(a) && !countStacksSeparately(a, b);
		}

		private boolean countStacksSeparately(ItemStack a, ItemStack b) {
			return (separateNbtData && !nbtDataMatches(a, b))
					|| (separateName && !nameMatches(a, b))
					|| (separateDurability && !durabilityMatches(a, b));
		}

		private boolean nbtDataMatches(ItemStack a, ItemStack b) {
			if (a.hasNbt() != b.hasNbt()) {
				return false;
			}
			if (!a.hasNbt() || a.getNbt() == null) {
				// neither itemStack has NBT data
				return true;
			}
			return a.getNbt().equals(b.getNbt());
		}

		private boolean nameMatches(ItemStack a, ItemStack b) {
			return a.getName().equals(b.getName());
		}

		private boolean durabilityMatches(ItemStack a, ItemStack b) {
			return a.getDamage() == b.getDamage();
		}
	}
}
