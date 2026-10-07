package dev.mthan.chordbind.gui;

import dev.mthan.chordbind.chord.Chord;
import dev.mthan.chordbind.chord.ChordBinding;
import dev.mthan.chordbind.chord.Conflicts;
import dev.mthan.chordbind.chord.Keys;
import dev.mthan.chordbind.config.ChordBindConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Adds or edits one binding. Clicking the chord button starts recording: hold the
 * keys you want, and the chord is captured once they are all released.
 */
public class EditBindingScreen extends Screen {
	private static final int MAX_COMMAND_LENGTH = 256;

	private final Screen parent;
	private final ChordBindConfig config;
	private final int index;

	private @Nullable Chord chord;
	private String command;
	private boolean recording;
	private final Set<Integer> recorded = new LinkedHashSet<>();

	private Button chordButton;
	private EditBox commandBox;
	private Button saveButton;

	/** @param index position of the binding to edit, or -1 to add a new one */
	public EditBindingScreen(Screen parent, ChordBindConfig config, int index) {
		super(Component.translatable(index < 0 ? "chordbind.screen.edit.title.add" : "chordbind.screen.edit.title.edit"));
		this.parent = parent;
		this.config = config;
		this.index = index;
		if (index >= 0) {
			ChordBinding binding = config.bindings().get(index);
			this.chord = binding.chord();
			this.command = binding.command();
		} else {
			this.command = "/";
		}
	}

	@Override
	protected void init() {
		int center = width / 2;
		int top = height / 2 - 60;

		chordButton = addRenderableWidget(Button.builder(Component.empty(), b -> startRecording())
			.bounds(center - 150, top + 12, 300, 20).build());

		commandBox = new EditBox(font, center - 150, top + 56, 300, 20, Component.translatable("chordbind.screen.edit.command"));
		commandBox.setMaxLength(MAX_COMMAND_LENGTH);
		commandBox.setValue(command);
		commandBox.setHint(Component.translatable("chordbind.screen.edit.command.hint").withStyle(EditBox.DEFAULT_HINT_STYLE));
		commandBox.setResponder(value -> {
			command = value;
			refresh();
		});
		addRenderableWidget(commandBox);

		saveButton = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> save())
			.bounds(center - 154, top + 120, 150, 20).build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> onClose())
			.bounds(center + 4, top + 120, 150, 20).build());

		refresh();
	}

	@Override
	protected void setInitialFocus() {
		setInitialFocus(chord == null ? chordButton : commandBox);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		int center = width / 2;
		int top = height / 2 - 60;
		graphics.centeredText(font, title, center, top - 20, -1);
		graphics.text(font, Component.translatable("chordbind.screen.edit.chord"), center - 150, top, 0xFFA0A0A0);
		graphics.text(font, Component.translatable("chordbind.screen.edit.command"), center - 150, top + 44, 0xFFA0A0A0);

		int y = top + 84;
		for (Component message : messages()) {
			graphics.textWithWordWrap(font, message, center - 150, y, 300, -1);
			y += font.wordWrapHeight(message, 300) + 2;
		}
	}

	// --- recording ---

	private void startRecording() {
		recording = true;
		recorded.clear();
		setFocused(null);
		refresh();
	}

	private void finishRecordingIfReleased() {
		if (recording && !recorded.isEmpty() && recorded.stream().noneMatch(Keys::isHeld)) {
			chord = Chord.of(recorded);
			recording = false;
			refresh();
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!recording) {
			return super.keyPressed(event);
		}
		if (event.isEscape() && recorded.isEmpty()) {
			recording = false;
			refresh();
		} else if (!event.isEscape()) {
			recorded.add(Keys.canonical(event.key()));
			refresh();
		}
		return true;
	}

	@Override
	public boolean keyReleased(KeyEvent event) {
		if (recording) {
			finishRecordingIfReleased();
			return true;
		}
		return super.keyReleased(event);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		return recording || super.charTyped(event);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (recording) {
			recording = false;
			refresh();
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void tick() {
		// Fallback in case a key release event was missed (e.g. window lost focus).
		finishRecordingIfReleased();
	}

	// --- state ---

	private void refresh() {
		if (chordButton == null) {
			return;
		}
		Component label;
		if (recording) {
			String held = recorded.isEmpty()
				? Component.translatable("chordbind.screen.edit.recording").getString()
				: Chord.of(recorded).displayName();
			label = Component.literal("> " + held + " <").withStyle(ChatFormatting.YELLOW);
		} else if (chord == null) {
			label = Component.translatable("chordbind.screen.edit.record");
		} else {
			label = Component.literal(chord.displayName());
		}
		chordButton.setMessage(label);
		saveButton.active = !recording && chord != null && !command.isBlank() && !isDuplicate();
	}

	private boolean isDuplicate() {
		return chord != null && Conflicts.isDuplicate(config.bindings(), chord, index);
	}

	private List<Component> messages() {
		List<Component> messages = new ArrayList<>();
		if (recording) {
			messages.add(Component.translatable("chordbind.screen.edit.recording.help").withStyle(ChatFormatting.GRAY));
			return messages;
		}
		if (chord != null) {
			if (isDuplicate()) {
				messages.add(Component.translatable("chordbind.conflict.duplicate").withStyle(ChatFormatting.RED));
			}
			List<String> mappings = Conflicts.keyMappingsUsing(chord);
			if (!mappings.isEmpty()) {
				messages.add(Component.translatable("chordbind.conflict.keymapping", String.join(", ", mappings))
					.withStyle(ChatFormatting.YELLOW));
			}
		}
		if (!command.isBlank()) {
			messages.add(Component.translatable(command.startsWith("/")
				? "chordbind.screen.edit.sends.command"
				: "chordbind.screen.edit.sends.chat").withStyle(ChatFormatting.GRAY));
		}
		return messages;
	}

	private void save() {
		if (chord == null || command.isBlank() || isDuplicate()) {
			return;
		}
		List<ChordBinding> updated = new ArrayList<>(config.bindings());
		ChordBinding binding = new ChordBinding(chord, command.strip());
		if (index >= 0) {
			updated.set(index, binding);
		} else {
			updated.add(binding);
		}
		config.setBindings(updated);
		onClose();
	}

	@Override
	public void onClose() {
		minecraft.gui.setScreen(parent);
	}
}
