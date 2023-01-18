package blazingtwist.itemcounts.config;

import blazingtwist.itemcounts.ItemCounts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

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

	public boolean shouldShowCount(PlayerEntity player, ItemStack stack) {
		return acceptanceCriteria.apply(player, stack);
	}

	private static boolean isMoreThanOne(PlayerEntity player, ItemStack stack) {
		return ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack) > 1;
	}

	private static boolean isMoreThanStack(PlayerEntity player, ItemStack stack) {
		return ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack) > stack.getMaxCount();
	}

	private static boolean isMoreThanHotbar(PlayerEntity player, ItemStack stack) {
		int totalCount = ItemCounts.getConfig().item_count_rules.getTotalItemCount(player, stack);
		int hotbarCount = ItemCounts.getConfig().item_count_rules.getHotbarItemCount(player, stack);
		return totalCount > hotbarCount;
	}

	@FunctionalInterface
	private interface CountDisplayPredicate {
		boolean apply(PlayerEntity player, ItemStack stack);
	}
}
