package com.ryanmichela.sshd.implementations;

import com.ryanmichela.sshd.SshdPlugin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

import com.ryanmichela.sshd.ConsoleShellFactory;
import com.ryanmichela.sshd.ConsoleLogFormatter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

public final class SSHDCommandSender implements CommandSender
{
	public ConsoleShellFactory.ConsoleShell console;

	@Override
	public void sendMessage(String message)
	{
		if (message.indexOf('\n') != 0)
			this.sendRawMessage(message);
		else
			Arrays.asList(message.split("\n")).forEach(this::sendMessage);
	}

	public void sendRawMessage(String message) 
	{
		if (this.console.ConsoleReader == null)
			return;
		/*
		try 
		{
			this.console.ConsoleReader.println(ConsoleLogFormatter.ColorizeString(message).replace("\n", "\n\r"));
		}
		catch (IOException e) 
		{
			SshdPlugin.instance.getLogger().log(Level.SEVERE, "Error sending message to SSHDCommandSender", e);
		}*/
	}

	@Override
	public void sendMessages(String... messages) 
	{
		Arrays.asList(messages).forEach(this::sendMessage);
	}

	@Override
	public void sendMessage(BaseComponent... message) 
	{
		sendMessage(BaseComponent.toLegacyText(message));
	}

	@Override
	public void sendMessage(BaseComponent message) 
	{
		sendMessage(message.toLegacyText());
	}

	@Override
	public String getName() 
	{
		return "SSHD CONSOLE";
	}

	@Override
	public Collection<String> getGroups() 
	{
		return Collections.emptySet();
	}

	@Override
	public void addGroups(String... groups) 
	{
		throw new UnsupportedOperationException("Console may not have groups");
	}

	@Override
	public void removeGroups(String... groups) 
	{
		throw new UnsupportedOperationException("Console may not have groups");
	}

	@Override
	public boolean hasPermission(String permission) 
	{
		return true;
	}

	@Override
	public void setPermission(String permission, boolean value) 
	{
		throw new UnsupportedOperationException("Console has all permissions");
	}

	@Override
	public Collection<String> getPermissions() 
	{
		return Collections.emptySet();
	}
}