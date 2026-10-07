package io.github.mthan.chordbind.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.mthan.chordbind.ChordBindClient;
import io.github.mthan.chordbind.gui.BindingListScreen;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new BindingListScreen(parent, ChordBindClient.config());
	}
}
