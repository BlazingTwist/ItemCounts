package blazingtwist.itemcounts.config;

import blazingtwist.itemcounts.util.ItemTypes;
import java.util.function.Function;
import net.minecraft.item.ItemStack;

public enum DurabilityItemOption {
	@AutoConfigEnum NONE(stack -> false),
	@AutoConfigEnum ALL(stack -> true),
	@AutoConfigEnum ENCHANTED(ItemStack::hasEnchantments),
	@AutoConfigEnum GOLD_DIAMOND_NETHERITE(stack -> ItemTypes.stackIsOfItem(stack, ItemTypes.goldTools, ItemTypes.diamondTools, ItemTypes.netheriteTools)),
	@AutoConfigEnum DIAMOND_NETHERITE(stack -> ItemTypes.stackIsOfItem(stack, ItemTypes.diamondTools, ItemTypes.netheriteTools));

	private final Function<ItemStack, Boolean> acceptanceCriteria;

	DurabilityItemOption(Function<ItemStack, Boolean> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean showDurabilityInsteadOfItemCount(ItemStack stack) {
		return acceptanceCriteria.apply(stack);
	}
}
