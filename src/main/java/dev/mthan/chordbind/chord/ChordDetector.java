package dev.mthan.chordbind.chord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Polls key state once per client tick and reports bindings whose chord just
 * became fully held. A chord fires once per press: it must be released (any of
 * its keys let go) before it can fire again.
 *
 * <p>When several chords become held together, only the most specific ones fire,
 * so holding Ctrl+Shift+E does not also fire a Ctrl+E binding.
 */
public class ChordDetector {
	private final Supplier<List<ChordBinding>> bindings;
	private final Consumer<ChordBinding> onFire;
	private final Set<Chord> heldLastTick = new HashSet<>();

	public ChordDetector(Supplier<List<ChordBinding>> bindings, Consumer<ChordBinding> onFire) {
		this.bindings = bindings;
		this.onFire = onFire;
	}

	/**
	 * @param active whether chords may fire right now (in-world, no screen open,
	 *               window focused). While inactive, held chords are still tracked
	 *               so closing a screen with keys held does not fire them.
	 */
	public void tick(boolean active) {
		List<ChordBinding> all = bindings.get();
		Set<Chord> heldNow = new HashSet<>();
		for (ChordBinding binding : all) {
			if (binding.chord().isHeld()) {
				heldNow.add(binding.chord());
			}
		}

		if (active) {
			List<ChordBinding> toFire = new ArrayList<>();
			for (ChordBinding binding : all) {
				Chord chord = binding.chord();
				if (heldNow.contains(chord) && !heldLastTick.contains(chord) && !isShadowed(chord, heldNow)) {
					toFire.add(binding);
				}
			}
			toFire.forEach(onFire);
		}

		heldLastTick.clear();
		heldLastTick.addAll(heldNow);
	}

	private static boolean isShadowed(Chord chord, Set<Chord> heldNow) {
		for (Chord other : heldNow) {
			if (chord.isStrictSubsetOf(other)) {
				return true;
			}
		}
		return false;
	}
}
