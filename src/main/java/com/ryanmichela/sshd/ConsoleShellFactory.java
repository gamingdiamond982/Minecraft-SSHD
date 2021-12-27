package com.ryanmichela.sshd;

import com.ryanmichela.sshd.ConsoleCommandCompleter;
import com.ryanmichela.sshd.ConsoleLogFormatter;
import com.ryanmichela.sshd.PermissionUtil;
import com.ryanmichela.sshd.FlushyOutputStream;
import com.ryanmichela.sshd.FlushyStreamHandler;
import com.ryanmichela.sshd.SshTerminal;
import com.ryanmichela.sshd.SshdPlugin;
import com.ryanmichela.sshd.StreamHandlerAppender;
import com.ryanmichela.sshd.implementations.SSHDCommandSender;
import jline.console.ConsoleReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.shell.ShellFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

public class ConsoleShellFactory implements ShellFactory 
{

	public Command createShell(ChannelSession cs) 
	{
		return new ConsoleShell();
	}

}