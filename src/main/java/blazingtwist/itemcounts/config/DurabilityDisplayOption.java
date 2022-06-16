package blazingtwist.itemcounts.config;

import java.util.function.Function;
import net.minecraft.item.ItemStack;

public enum DurabilityDisplayOption {
	@AutoConfigEnum NEVER(stack -> false),
	@AutoConfigEnum ALWAYS(stack -> true),
	@AutoConfigEnum DAMAGED(ItemStack::isDamaged),
	@AutoConfigEnum ALMOST_BROKEN(stack -> stack.isDamageable() && ((float) stack.getDamage() / stack.getMaxDamage()) > 0.85f);

	private final Function<ItemStack, Boolean> acceptanceCriteria;

	DurabilityDisplayOption(Function<ItemStack, Boolean> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean shouldShowDurability(ItemStack stack) {
		return acceptanceCriteria.apply(stack);
	}
}
