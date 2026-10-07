package dev.mthan.chordbind.chord;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

/**
 * Keyboard helpers. Key codes are the values used by {@link InputConstants}:
 * GLFW key codes up to 26.2, SDL scancodes from 26.3. Configs store key names,
 * which are the same across versions.
 */
public final class Keys {
	//? if >=26.3 {
	/*public static final InputConstants.Type KEYBOARD = InputConstants.Type.KEYBOARD;
	private static final int LEFT_META = InputConstants.KEY_LGUI;
	private static final int RIGHT_META = InputConstants.KEY_RGUI;
	*///?} else {
	public static final InputConstants.Type KEYBOARD = InputConstants.Type.KEYSYM;
	private static final int LEFT_META = InputConstants.KEY_LSUPER;
	private static final int RIGHT_META = InputConstants.KEY_RSUPER;
	//?}

	private Keys() {
	}

	/** Maps right-hand modifiers to their left-hand twin so either side satisfies a chord. */
	public static int canonical(int key) {
		return switch (key) {
			case InputConstants.KEY_RCONTROL -> InputConstants.KEY_LCONTROL;
			case InputConstants.KEY_RSHIFT -> InputConstants.KEY_LSHIFT;
			case InputConstants.KEY_RALT -> InputConstants.KEY_LALT;
			case RIGHT_META -> LEFT_META;
			default -> key;
		};
	}

	public static boolean isModifier(int key) {
		return switch (canonical(key)) {
			case InputConstants.KEY_LCONTROL, InputConstants.KEY_LSHIFT, InputConstants.KEY_LALT, LEFT_META -> true;
			default -> false;
		};
	}

	/** True if the key (or, for modifiers, either side of it) is physically held. */
	public static boolean isHeld(int key) {
		return switch (canonical(key)) {
			case InputConstants.KEY_LCONTROL -> down(InputConstants.KEY_LCONTROL) || down(InputConstants.KEY_RCONTROL);
			case InputConstants.KEY_LSHIFT -> down(InputConstants.KEY_LSHIFT) || down(InputConstants.KEY_RSHIFT);
			case InputConstants.KEY_LALT -> down(InputConstants.KEY_LALT) || down(InputConstants.KEY_RALT);
			case LEFT_META -> down(LEFT_META) || down(RIGHT_META);
			default -> down(key);
		};
	}

	public static String displayName(int key) {
		return KEYBOARD.getOrCreate(key).getDisplayName().getString();
	}

	/** Stable name used in the config file, e.g. "key.keyboard.left.control". */
	public static String name(int key) {
		return KEYBOARD.getOrCreate(key).getName();
	}

	/** Parses a name produced by {@link #name(int)}; returns -1 if it is not a known keyboard key. */
	public static int fromName(String name) {
		try {
			InputConstants.Key key = InputConstants.getKey(name);
			if (key.getType() == KEYBOARD && key != InputConstants.UNKNOWN) {
				return key.getValue();
			}
		} catch (RuntimeException ignored) {
		}
		return -1;
	}

	private static boolean down(int key) {
		//? if >=26.3 {
		/*return InputConstants.isKeyDown(key);
		*///?} else {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
		//?}
	}
}
