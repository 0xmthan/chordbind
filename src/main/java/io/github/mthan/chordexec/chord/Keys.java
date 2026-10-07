package io.github.mthan.chordexec.chord;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * Keyboard helpers. Key codes are the values used by {@link InputConstants}
 * (SDL scancodes since Minecraft 26.x), never raw GLFW codes.
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
			case InputConstants.KEY_RGUI -> InputConstants.KEY_LGUI;
			default -> key;
		};
	}

	public static boolean isModifier(int key) {
		return switch (canonical(key)) {
			case InputConstants.KEY_LCONTROL, InputConstants.KEY_LSHIFT,
				 InputConstants.KEY_LALT, InputConstants.KEY_LGUI -> true;
			default -> false;
		};
	}

	/** True if the key (or, for modifiers, either side of it) is physically held. */
	public static boolean isHeld(int key) {
		return switch (canonical(key)) {
			case InputConstants.KEY_LCONTROL -> down(InputConstants.KEY_LCONTROL) || down(InputConstants.KEY_RCONTROL);
			case InputConstants.KEY_LSHIFT -> down(InputConstants.KEY_LSHIFT) || down(InputConstants.KEY_RSHIFT);
			case InputConstants.KEY_LALT -> down(InputConstants.KEY_LALT) || down(InputConstants.KEY_RALT);
			case InputConstants.KEY_LGUI -> down(InputConstants.KEY_LGUI) || down(InputConstants.KEY_RGUI);
			default -> down(key);
		};
	}

	public static String displayName(int key) {
		return InputConstants.Type.KEYBOARD.getOrCreate(key).getDisplayName().getString();
	}

	private static boolean down(int key) {
		return InputConstants.isKeyDown(key);
	}
}
