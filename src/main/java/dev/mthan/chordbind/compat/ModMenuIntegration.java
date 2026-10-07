package dev.mthan.chordbind.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.mthan.chordbind.ChordBindClient;
import dev.mthan.chordbind.gui.BindingListScreen;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new BindingListScreen(parent, ChordBindClient.config());
	}
}
