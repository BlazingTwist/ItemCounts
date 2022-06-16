package blazingtwist.itemcounts.config;

import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;

public enum CountDisplayOption {
	@AutoConfigEnum NEVER((stack, count) -> false),
	@AutoConfigEnum ALWAYS((stack, count) -> true),
	@AutoConfigEnum MORE_THAN_ONE((stack, count) -> count > 1),
	@AutoConfigEnum MORE_THAN_STACK((stack, count) -> count > stack.getMaxCount());

	private final BiFunction<ItemStack, Integer, Boolean> acceptanceCriteria;

	CountDisplayOption(BiFunction<ItemStack, Integer, Boolean> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean shouldShowCount(ItemStack stack, int totalCount) {
		return acceptanceCriteria.apply(stack, totalCount);
	}
}
