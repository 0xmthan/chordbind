package dev.mthan.chordbind;

import com.mojang.blaze3d.platform.InputConstants;
import dev.mthan.chordbind.chord.ChordDetector;
import dev.mthan.chordbind.chord.Keys;
import dev.mthan.chordbind.config.ChordBindConfig;
import dev.mthan.chordbind.gui.BindingListScreen;
import dev.mthan.chordbind.gui.Screens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChordBindClient implements ClientModInitializer {
	public static final String MOD_ID = "chordbind";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static ChordBindConfig config;

	public static ChordBindConfig config() {
		return config;
	}

	@Override
	public void onInitializeClient() {
		config = new ChordBindConfig(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json"));
		config.load();

		KeyMapping openConfig = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.chordbind.open_config",
			Keys.KEYBOARD,
			InputConstants.UNKNOWN.getValue(),
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"))
		));

		Minecraft client = Minecraft.getInstance();
		ChordDetector detector = new ChordDetector(config::bindings, binding -> {
			LOGGER.debug("Chord {} fired: {}", binding.chord().displayName(), binding.command());
			CommandSender.send(client, binding.command());
		});

		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			while (openConfig.consumeClick()) {
				Screens.open(mc, new BindingListScreen(null, config));
			}
			detector.tick(mc.player != null && Screens.current(mc) == null && mc.isWindowActive());
		});
	}
}
