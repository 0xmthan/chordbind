package io.github.mthan.chordbind.chord;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

/**
 * Keyboard helpers. Key codes are the values used by {@link InputConstants}
 * (GLFW key codes on 26.2), never raw GLFW codes.
 */
public final class Keys {
	private Keys() {
	}

	/** Maps right-hand modifiers to their left-hand twin so either side satisfies a chord. */
	public static int canonical(int key) {
		return switch (key) {
			case InputConstants.KEY_RCONTROL -> InputConstants.KEY_LCONTROL;
			case InputConstants.KEY_RSHIFT -> InputConstants.KEY_LSHIFT;
			case InputConstants.KEY_RALT -> InputConstants.KEY_LALT;
			case InputConstants.KEY_RSUPER -> InputConstants.KEY_LSUPER;
			default -> key;
		};
	}

	public static boolean isModifier(int key) {
		return switch (canonical(key)) {
			case InputConstants.KEY_LCONTROL, InputConstants.KEY_LSHIFT,
				 InputConstants.KEY_LALT, InputConstants.KEY_LSUPER -> true;
			default -> false;
		};
	}

	/** True if the key (or, for modifiers, either side of it) is physically held. */
	public static boolean isHeld(int key) {
		return switch (canonical(key)) {
			case InputConstants.KEY_LCONTROL -> down(InputConstants.KEY_LCONTROL) || down(InputConstants.KEY_RCONTROL);
			case InputConstants.KEY_LSHIFT -> down(InputConstants.KEY_LSHIFT) || down(InputConstants.KEY_RSHIFT);
			case InputConstants.KEY_LALT -> down(InputConstants.KEY_LALT) || down(InputConstants.KEY_RALT);
			case InputConstants.KEY_LSUPER -> down(InputConstants.KEY_LSUPER) || down(InputConstants.KEY_RSUPER);
			default -> down(key);
		};
	}

	public static String displayName(int key) {
		return InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
	}

	/** Stable name used in the config file, e.g. "key.keyboard.left.control". */
	public static String name(int key) {
		return InputConstants.Type.KEYSYM.getOrCreate(key).getName();
	}

	/** Parses a name produced by {@link #name(int)}; returns -1 if it is not a known keyboard key. */
	public static int fromName(String name) {
		try {
			InputConstants.Key key = InputConstants.getKey(name);
			if (key.getType() == InputConstants.Type.KEYSYM && key != InputConstants.UNKNOWN) {
				return key.getValue();
			}
		} catch (RuntimeException ignored) {
		}
		return -1;
	}

	private static boolean down(int key) {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
	}
}
