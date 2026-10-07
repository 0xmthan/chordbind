package dev.mthan.chordbind.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;

/** Screen access, which moved from {@link Minecraft} to its {@code gui} in 26.2. */
public final class Screens {
	private Screens() {
	}

	public static @Nullable Screen current(Minecraft client) {
		//? if >=26.2 {
		return client.gui.screen();
		//?} else {
		/*return client.screen;
		*///?}
	}

	public static void open(Minecraft client, @Nullable Screen screen) {
		//? if >=26.2 {
		client.gui.setScreen(screen);
		//?} else {
		/*client.setScreen(screen);
		*///?}
	}
}
