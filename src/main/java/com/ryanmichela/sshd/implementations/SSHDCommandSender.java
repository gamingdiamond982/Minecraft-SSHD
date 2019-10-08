package com.ryanmichela.sshd.implementations;

import com.ryanmichela.sshd.SshdPlugin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

/*
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
*/

import com.ryanmichela.sshd.ConsoleShellFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SSHDCommandSender implements CommandSender {
	private static final SSHDCommandSender instance = new SSHDCommandSender();

	public ConsoleShellFactory.ConsoleShell console;

	@Override
	public void sendMessage(String message) {
		ProxyServer.getInstance().getLogger().info(message);
	}

	public void sendRawMessage(String message) {
		// What the fuck does this code even do? Are we sending to one client or all of
		// them?
		if (this.console.ConsoleReader == null)
			return;
		try {
			ProxyServer.getInstance().getLogger().info(message);
			this.console.ConsoleReader.println(ChatColor.stripColor(message));
		} catch (IOException e) {
			SshdPlugin.instance.getLogger().log(Level.SEVERE, "Error sending message to SSHDCommandSender", e);
		}
	}

	@Override
	public void sendMessages(String... messages) {
		Arrays.asList(messages).forEach(this::sendMessage);
	}

	@Override
	public void sendMessage(BaseComponent... message) {
		sendMessage(BaseComponent.toLegacyText(message));
	}

	@Override
	public void sendMessage(BaseComponent message) {
		sendMessage(message.toLegacyText());
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public Collection<String> getGroups() {
		return Collections.emptySet();
	}

	@Override
	public void addGroups(String... groups) {
		throw new UnsupportedOperationException("Console may not have groups");
	}

	@Override
	public void removeGroups(String... groups) {
		throw new UnsupportedOperationException("Console may not have groups");
	}

	@Override
	public boolean hasPermission(String permission) {
		return true;
	}

	@Override
	public void setPermission(String permission, boolean value) {
		throw new UnsupportedOperationException("Console has all permissions");
	}

	@Override
	public Collection<String> getPermissions() {
		return Collections.emptySet();
	}
}