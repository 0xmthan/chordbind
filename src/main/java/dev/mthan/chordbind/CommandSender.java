package dev.mthan.chordbind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Sends text exactly as if the player had typed it in chat, so normal server
 * permissions apply. Text starting with "/" is sent as a command.
 */
public final class CommandSender {
	private CommandSender() {
	}

	/** Same cleanup vanilla chat applies: trim, collapse whitespace, cap at 256 chars. */
	public static String normalize(String text) {
		return StringUtil.trimChatMessage(StringUtils.normalizeSpace(text.trim()));
	}

	/** True if the text would actually send something (not blank, not a bare "/"). */
	public static boolean isSendable(String text) {
		String normalized = normalize(text);
		return !normalized.isEmpty() && !normalized.equals("/");
	}

	public static void send(Minecraft client, String text) {
		ClientPacketListener connection = client.getConnection();
		String normalized = normalize(text);
		if (connection == null || !isSendable(normalized)) {
			return;
		}
		if (normalized.startsWith("/")) {
			connection.sendCommand(normalized.substring(1));
		} else {
			connection.sendChat(normalized);
		}
	}
}
