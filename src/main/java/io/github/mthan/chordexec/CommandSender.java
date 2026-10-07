package io.github.mthan.chordexec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

/**
 * Sends text exactly as if the player had typed it in chat, so normal server
 * permissions apply. Text starting with "/" is sent as a command.
 */
public final class CommandSender {
	private CommandSender() {
	}

	public static void send(Minecraft client, String text) {
		ClientPacketListener connection = client.getConnection();
		if (connection == null || text.isBlank()) {
			return;
		}
		if (text.startsWith("/")) {
			connection.sendCommand(text.substring(1));
		} else {
			connection.sendChat(text);
		}
	}
}
