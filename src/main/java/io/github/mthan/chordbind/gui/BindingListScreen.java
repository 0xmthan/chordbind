package io.github.mthan.chordbind.gui;

import io.github.mthan.chordbind.chord.ChordBinding;
import io.github.mthan.chordbind.chord.Conflicts;
import io.github.mthan.chordbind.config.ChordBindConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Lists all bindings with edit/delete buttons, plus an "Add" button. */
public class BindingListScreen extends Screen {
	private final @Nullable Screen parent;
	private final ChordBindConfig config;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private BindingList list;

	public BindingListScreen(@Nullable Screen parent, ChordBindConfig config) {
		super(Component.translatable("chordbind.screen.list.title"));
		this.parent = parent;
		this.config = config;
	}

	@Override
	protected void init() {
		layout.addTitleHeader(title, font);
		list = layout.addToContents(new BindingList(minecraft));
		LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
		footer.addChild(Button.builder(Component.translatable("chordbind.screen.list.add"),
			button -> openEditor(-1)).build());
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).build());
		layout.visitWidgets(this::addRenderableWidget);
		repositionElements();
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
		list.updateSize(width, layout);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		if (config.bindings().isEmpty()) {
			graphics.centeredText(font, Component.translatable("chordbind.screen.list.empty"),
				width / 2, layout.getHeaderHeight() + 20, 0xFFA0A0A0);
		}
	}

	@Override
	public void onClose() {
		minecraft.gui.setScreen(parent);
	}

	private void openEditor(int index) {
		minecraft.gui.setScreen(new EditBindingScreen(this, config, index));
	}

	private void confirmDelete(int index) {
		ChordBinding binding = config.bindings().get(index);
		minecraft.gui.setScreen(new ConfirmScreen(confirmed -> {
			if (confirmed) {
				List<ChordBinding> updated = new ArrayList<>(config.bindings());
				updated.remove(index);
				config.setBindings(updated);
			}
			minecraft.gui.setScreen(this);
		}, Component.translatable("chordbind.screen.list.delete.title"),
			Component.literal(binding.chord().displayName() + " → " + binding.command())));
	}

	private class BindingList extends ContainerObjectSelectionList<BindingList.Entry> {
		BindingList(Minecraft minecraft) {
			super(minecraft, BindingListScreen.this.width, layout.getContentHeight(), layout.getHeaderHeight(), 24);
			List<ChordBinding> bindings = config.bindings();
			for (int i = 0; i < bindings.size(); i++) {
				addEntry(new Entry(i, bindings.get(i), Conflicts.isDuplicate(bindings, bindings.get(i).chord(), i)));
			}
		}

		@Override
		public int getRowWidth() {
			return 380;
		}

		class Entry extends ContainerObjectSelectionList.Entry<Entry> {
			private static final int BUTTON_WIDTH = 50;
			private final ChordBinding binding;
			private final boolean duplicate;
			private final Button editButton;
			private final Button deleteButton;

			Entry(int index, ChordBinding binding, boolean duplicate) {
				this.binding = binding;
				this.duplicate = duplicate;
				editButton = Button.builder(Component.translatable("chordbind.screen.list.edit"), b -> openEditor(index))
					.size(BUTTON_WIDTH, 20).build();
				deleteButton = Button.builder(Component.translatable("chordbind.screen.list.delete"), b -> confirmDelete(index))
					.size(BUTTON_WIDTH, 20).build();
				if (duplicate) {
					editButton.setTooltip(Tooltip.create(Component.translatable("chordbind.conflict.duplicate")));
				}
			}

			@Override
			public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
				int buttonY = getContentY() + (getContentHeight() - 20) / 2;
				int deleteX = getContentX() + getContentWidth() - BUTTON_WIDTH;
				int editX = deleteX - 4 - BUTTON_WIDTH;
				deleteButton.setPosition(deleteX, buttonY);
				editButton.setPosition(editX, buttonY);
				deleteButton.extractRenderState(graphics, mouseX, mouseY, a);
				editButton.extractRenderState(graphics, mouseX, mouseY, a);

				int textWidth = editX - getContentX() - 8;
				int chordWidth = textWidth * 2 / 5;
				int textY = getContentYMiddle() - font.lineHeight / 2;
				String chord = font.plainSubstrByWidth(binding.chord().displayName(), chordWidth);
				Component chordText = duplicate
					? Component.literal("⚠ " + chord).withStyle(ChatFormatting.YELLOW)
					: Component.literal(chord);
				graphics.text(font, chordText, getContentX(), textY, -1);
				String command = font.plainSubstrByWidth(binding.command(), textWidth - chordWidth - 8);
				graphics.text(font, command, getContentX() + chordWidth + 8, textY, 0xFFA0A0A0);
			}

			@Override
			public List<? extends GuiEventListener> children() {
				return List.of(editButton, deleteButton);
			}

			@Override
			public List<? extends NarratableEntry> narratables() {
				return List.of(editButton, deleteButton);
			}
		}
	}
}
