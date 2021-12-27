package com.ryanmichela.sshd;

import com.ryanmichela.sshd.implementations.SSHDCommandSender;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copyright 2013 Ryan Michela
 */
public class ConsoleCommandFactory implements CommandFactory 
{

    @Override
    public Command createCommand(ChannelSession cs, String command) 
    {
        return new ConsoleCommand(command);
    }

}
