package dev.mthan.chordbind.chord;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public final class Conflicts {
	private Conflicts() {
	}

	/** True if another binding (at a different index) uses exactly the same chord. */
	public static boolean isDuplicate(List<ChordBinding> bindings, Chord chord, int ignoreIndex) {
		for (int i = 0; i < bindings.size(); i++) {
			if (i != ignoreIndex && bindings.get(i).chord().equals(chord)) {
				return true;
			}
		}
		return false;
	}

	/** Names of game/mod key mappings bound to one of the chord's non-modifier keys. */
	public static List<String> keyMappingsUsing(Chord chord) {
		List<String> names = new ArrayList<>();
		for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
			InputConstants.Key bound = KeyMappingHelper.getBoundKeyOf(mapping);
			if (bound.getType() != InputConstants.Type.KEYSYM) {
				continue;
			}
			int key = Keys.canonical(bound.getValue());
			if (!Keys.isModifier(key) && chord.keys().contains(key)) {
				names.add(net.minecraft.network.chat.Component.translatable(mapping.getName()).getString());
			}
		}
		return names;
	}
}
