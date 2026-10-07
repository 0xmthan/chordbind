package dev.mthan.chordbind.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.mthan.chordbind.ChordBindClient;
import dev.mthan.chordbind.chord.Chord;
import dev.mthan.chordbind.chord.ChordBinding;
import dev.mthan.chordbind.chord.Keys;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Bindings stored in {@code config/chordbind.json}. Keys are saved by name
 * (e.g. "key.keyboard.g") rather than code so the file survives input backend changes.
 */
public class ChordBindConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private final Path path;
	private List<ChordBinding> bindings = List.of();

	public ChordBindConfig(Path path) {
		this.path = path;
	}

	public List<ChordBinding> bindings() {
		return bindings;
	}

	public void setBindings(List<ChordBinding> bindings) {
		this.bindings = List.copyOf(bindings);
		save();
	}

	public void load() {
		if (!Files.exists(path)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(path)) {
			FileData data = GSON.fromJson(reader, FileData.class);
			List<ChordBinding> loaded = new ArrayList<>();
			if (data != null && data.bindings != null) {
				for (EntryData entry : data.bindings) {
					ChordBinding binding = toBinding(entry);
					if (binding != null) {
						loaded.add(binding);
					}
				}
			}
			bindings = List.copyOf(loaded);
		} catch (IOException | JsonParseException e) {
			Path backup = path.resolveSibling(path.getFileName() + ".bak");
			ChordBindClient.LOGGER.error("Failed to read {}, backing it up to {} and starting with no bindings", path, backup, e);
			try {
				Files.copy(path, backup, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException copyError) {
				ChordBindClient.LOGGER.error("Failed to back up {}", path, copyError);
			}
		}
	}

	public void save() {
		List<EntryData> entries = bindings.stream()
			.map(b -> new EntryData(b.chord().keys().stream().map(Keys::name).toList(), b.command()))
			.toList();
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(new FileData(entries), writer);
			}
		} catch (IOException e) {
			ChordBindClient.LOGGER.error("Failed to write {}", path, e);
		}
	}

	private static ChordBinding toBinding(EntryData entry) {
		if (entry.keys == null || entry.keys.isEmpty() || entry.command == null) {
			ChordBindClient.LOGGER.warn("Skipping incomplete binding in config: {}", entry);
			return null;
		}
		List<Integer> keys = new ArrayList<>();
		for (String name : entry.keys) {
			int key = Keys.fromName(name);
			if (key < 0) {
				ChordBindClient.LOGGER.warn("Skipping binding with unknown key '{}': {}", name, entry);
				return null;
			}
			keys.add(key);
		}
		return new ChordBinding(Chord.of(keys), entry.command);
	}

	private record FileData(List<EntryData> bindings) {
	}

	private record EntryData(List<String> keys, String command) {
	}
}
