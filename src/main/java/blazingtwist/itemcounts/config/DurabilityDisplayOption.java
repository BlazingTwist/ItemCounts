package blazingtwist.itemcounts.config;

import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public enum DurabilityDisplayOption {
	@AutoConfigEnum NEVER(stack -> false),
	@AutoConfigEnum ALWAYS(stack -> true),
	@AutoConfigEnum DAMAGED(ItemStack::isDamaged),
	@AutoConfigEnum ALMOST_BROKEN(stack -> stack.isDamageableItem() && ((float) stack.getDamageValue() / stack.getMaxDamage()) > 0.85f);

	private final Function<ItemStack, Boolean> acceptanceCriteria;

	DurabilityDisplayOption(Function<ItemStack, Boolean> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean shouldShowDurability(ItemStack stack) {
		return acceptanceCriteria.apply(stack);
	}
}
