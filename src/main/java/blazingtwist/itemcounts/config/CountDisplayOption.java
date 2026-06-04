package blazingtwist.itemcounts.config;

import blazingtwist.itemcounts.ItemCounts;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public enum CountDisplayOption {
	@AutoConfigEnum NEVER((player, stack) -> false),
	@AutoConfigEnum ALWAYS((player, stack) -> true),
	@AutoConfigEnum MORE_THAN_ONE(CountDisplayOption::isMoreThanOne),
	@AutoConfigEnum MORE_THAN_STACK(CountDisplayOption::isMoreThanStack),
	@AutoConfigEnum MORE_THAN_HOTBAR(CountDisplayOption::isMoreThanHotbar);

	private final CountDisplayPredicate acceptanceCriteria;

	CountDisplayOption(CountDisplayPredicate acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean shouldShowCount(Player player, ItemStack stack) {
		return acceptanceCriteria.apply(player, stack);
	}

	private static boolean isMoreThanOne(Player player, ItemStack stack) {
		return ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack) > 1;
	}

	private static boolean isMoreThanStack(Player player, ItemStack stack) {
		return ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack) > stack.getMaxStackSize();
	}

	private static boolean isMoreThanHotbar(Player player, ItemStack stack) {
		int totalCount = ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack);
		int hotbarCount = ItemCounts.getConfig().item_count_rules.getHotbarItemCount(player, stack);
		return totalCount > hotbarCount;
	}

	@FunctionalInterface
	private interface CountDisplayPredicate {
		boolean apply(Player player, ItemStack stack);
	}
}
