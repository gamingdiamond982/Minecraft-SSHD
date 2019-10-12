package com.ryanmichela.sshd.implementations;

import com.ryanmichela.sshd.SshdPlugin;
import com.ryanmichela.sshd.ConsoleShellFactory;
import com.ryanmichela.sshd.ConsoleLogFormatter;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.Optional;
import java.util.List;

// This is gonna be a mess.
public class SSHDCommandSender implements ConsoleSource
{
	private MessageChannel mc = MessageChannel.TO_CONSOLE;
	private Subject subjectDelegate;
	// Set by the upstream allocating function
	public ConsoleShellFactory.ConsoleShell console;

	// This is an override for Sponge to work with the SSH consoles.
	public void sendMessage(Text message)
	{
		this.sendRawMessage(message.toPlain() + "\r");
	}

	// Back port from Spigot/BungeeCord-style API calls.
	public void sendMessage(String message) 
	{
		this.sendRawMessage(message + "\r");
	}

	public void sendRawMessage(String message) 
	{
		// What the fuck does this code even do? Are we sending to one client or all of them?
		if (this.console.ConsoleReader == null)
			return;
		try 
		{
			this.console.ConsoleReader.println(ConsoleLogFormatter.ColorizeString(message).replace("\n", "\n\r"));
			this.console.ConsoleReader.print(this.console.ConsoleReader.RESET_LINE + "");
            this.console.ConsoleReader.flush();
            try 
            {
                this.console.ConsoleReader.drawLine();
            }
             catch (Throwable ex) 
            {
                this.console.ConsoleReader.getCursorBuffer().clear();
            }
            this.console.ConsoleReader.flush();
		} 
		catch (IOException e) 
		{
			SshdPlugin.GetLogger().error("Error sending message to SSHDCommandSender", e);
		}
	}

	public void sendMessage(String[] messages) 
	{
		Arrays.asList(messages).forEach(this::sendMessage);
	}

	public MessageChannel getMessageChannel()
	{
		return mc;
	}

	public void setMessageChannel(MessageChannel channel)
	{
		mc = channel;
	}

	public String getName() {
		return "SSHD Console";
	}

@Override
		public String getIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public Set<Context> getActiveContexts() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public boolean isSubjectDataPersisted() {
			// TODO Auto-generated method stub
			return false;
		}
	
		@Override
		public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
			// TODO Auto-generated method stub
			return false;
		}
	
		@Override
		public SubjectData getTransientSubjectData() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public SubjectData getSubjectData() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public Tristate getPermissionValue(Set<Context> contexts, String permission) {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public List<SubjectReference> getParents(Set<Context> contexts) {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public Optional<String> getOption(Set<Context> contexts, String key) {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public SubjectCollection getContainingCollection() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public Optional<CommandSource> getCommandSource() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public SubjectReference asSubjectReference() {
			// TODO Auto-generated method stub
			return null;
		}
}
