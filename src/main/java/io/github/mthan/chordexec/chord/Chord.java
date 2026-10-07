package io.github.mthan.chordexec.chord;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A set of keys that must all be held at the same time. Order does not matter;
 * modifiers are stored in their canonical (left-hand) form.
 */
public record Chord(List<Integer> keys) {
	public Chord {
		keys = keys.stream().map(Keys::canonical).distinct().sorted().toList();
		if (keys.isEmpty()) {
			throw new IllegalArgumentException("A chord needs at least one key");
		}
	}

	public static Chord of(Integer... keys) {
		return new Chord(List.of(keys));
	}

	public static Chord of(Collection<Integer> keys) {
		return new Chord(List.copyOf(keys));
	}

	public boolean isHeld() {
		for (int key : keys) {
			if (!Keys.isHeld(key)) {
				return false;
			}
		}
		return true;
	}

	/** True if every key of this chord is part of {@code other} and other has more keys. */
	public boolean isStrictSubsetOf(Chord other) {
		return other.keys.size() > keys.size() && other.keys.containsAll(keys);
	}

	/** Human-readable form, modifiers first, e.g. "Left Control + Left Shift + E". */
	public String displayName() {
		return keys.stream()
			.sorted((a, b) -> Boolean.compare(Keys.isModifier(b), Keys.isModifier(a)))
			.map(Keys::displayName)
			.collect(Collectors.joining(" + "));
	}
}
