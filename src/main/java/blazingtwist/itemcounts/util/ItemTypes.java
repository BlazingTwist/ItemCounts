package blazingtwist.itemcounts.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemTypes {
	public static final Item[] goldTools = {
			Items.GOLDEN_AXE,
			Items.GOLDEN_HOE,
			Items.GOLDEN_PICKAXE,
			Items.GOLDEN_SHOVEL,
			Items.GOLDEN_SWORD
	};

	public static final Item[] diamondTools = {
			Items.DIAMOND_AXE,
			Items.DIAMOND_HOE,
			Items.DIAMOND_PICKAXE,
			Items.DIAMOND_SHOVEL,
			Items.DIAMOND_SWORD
	};

	public static final Item[] netheriteTools = {
			Items.NETHERITE_AXE,
			Items.NETHERITE_HOE,
			Items.NETHERITE_PICKAXE,
			Items.NETHERITE_SHOVEL,
			Items.NETHERITE_SWORD
	};

	public static boolean stackIsOfItem(ItemStack stack, Item[]... items) {
		int rawItemID = Item.getRawId(stack.getItem());
		for (Item[] itemSubList : items) {
			for (Item item : itemSubList) {
				if (Item.getRawId(item) == rawItemID) {
					return true;
				}
			}
		}
		return false;
	}
}
