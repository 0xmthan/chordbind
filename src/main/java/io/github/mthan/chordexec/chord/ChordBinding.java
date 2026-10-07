package io.github.mthan.chordexec.chord;

/** A chord mapped to the command or chat message it sends. */
public record ChordBinding(Chord chord, String command) {
}
