package io.github.mthan.chordexec;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mthan.chordexec.chord.Chord;
import io.github.mthan.chordexec.chord.ChordBinding;
import io.github.mthan.chordexec.chord.ChordDetector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChordExecClient implements ClientModInitializer {
	public static final String MOD_ID = "chordexec";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Temporary hardcoded binding until config persistence lands.
	private static final List<ChordBinding> TEST_BINDINGS = List.of(
		new ChordBinding(Chord.of(InputConstants.KEY_LCONTROL, InputConstants.KEY_G), "/help")
	);

	@Override
	public void onInitializeClient() {
		Minecraft client = Minecraft.getInstance();
		ChordDetector detector = new ChordDetector(() -> TEST_BINDINGS, binding -> {
			LOGGER.debug("Chord {} fired: {}", binding.chord().displayName(), binding.command());
			CommandSender.send(client, binding.command());
		});

		ClientTickEvents.END_CLIENT_TICK.register(mc -> detector.tick(
			mc.player != null && mc.gui.screen() == null && mc.isWindowActive()
		));
	}
}
