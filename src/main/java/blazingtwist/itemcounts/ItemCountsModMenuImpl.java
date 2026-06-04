package blazingtwist.itemcounts;

import blazingtwist.itemcounts.config.ItemCountsConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;

public class ItemCountsModMenuImpl implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfigClient.getConfigScreen(ItemCountsConfig.class, parent).get();
	}
}
