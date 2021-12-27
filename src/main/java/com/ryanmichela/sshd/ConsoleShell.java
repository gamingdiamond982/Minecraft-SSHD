package com.ryanmichela.sshd;

import com.ryanmichela.sshd.implementations.SSHDCommandSender;
import jline.console.ConsoleReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.bukkit.Bukkit;
import jline.console.ConsoleReader;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

public class ConsoleShell extends ConsoleCommand
{
    StreamHandlerAppender streamHandlerAppender;

    @Override
    public void destroy(ChannelSession cs) {((Logger) LogManager.getRootLogger()).removeAppender(this.streamHandlerAppender);}

    @Override
    public void start(ChannelSession cs, Environment environment) throws  IOException {
        this.ConsoleReader = new ConsoleReader(in, new FlushyOutputStream(out), new SshTerminal());
        this.ConsoleReader.setExpandEvents(true);
        this.ConsoleReader.addCompleter(new ConsoleCommandCompleter());

        StreamHandler streamHandler = new FlushyStreamHandler(out, new ConsoleLogFormatter(), this.ConsoleReader);
        this.streamHandlerAppender = new StreamHandlerAppender(streamHandler);

        ((Logger) LogManager.getRootLogger()).addAppender(this.streamHandlerAppender);
        super.start(cs, environment);
    }

    @Override
    public void run()
    {
        try
        {
            if (!SshdPlugin.instance.getConfig().getString("Mode", "DEFAULT").equals("RPC"))
                printPreamble(this.ConsoleReader);
            while (true)
            {
                String command = this.ConsoleReader.readLine("\r>", null);
                // The user sent CTRL+D to close the shell, terminate the session.
                if (command == null)
                    break;
                // Skip someone spamming enter
                if (command.trim().isEmpty())
                    continue;
                // User wants to exit
                if (command.equals("exit") || command.equals("quit"))
                    break;
                // Clear the text from the screen (on supported terminals)
                if (command.equals("cls"))
                {
                    this.ConsoleReader.clearScreen();
                    this.ConsoleReader.drawLine();
                    this.ConsoleReader.flush();
                    continue;
                }
                // Hide the mkpasswd command input from other users.

                Optional<String> optcred = PermissionUtil.GetCredential(this.Username, "console");

                if (optcred.isPresent() && !optcred.get().contains("W"))
                    continue;
                Bukkit.getScheduler().runTask(SshdPlugin.instance, this.run_command(Bukkit.getConsoleSender(), command));

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            SshdPlugin.instance.getLogger().log(Level.SEVERE, "Error processing command from SSH", e);
        }
        finally
        {
            SshdPlugin.instance.getLogger().log(Level.INFO, this.Username + " disconnected from SSH.");
            callback.onExit(0);

        }
    }

    private String GetHostname()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            SshdPlugin.instance.getLogger().log(Level.INFO, "The above stacktrace can be ignored, you likely have a misconfigured system hosts file.");
            return "Unknown";
        }
    }

    private void printPreamble(ConsoleReader cr) throws IOException
    {
        File f = new File(SshdPlugin.instance.getDataFolder(), "motd.txt");
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(f));

            String st;
            while ((st = br.readLine()) != null)
                cr.println(ConsoleLogFormatter.ColorizeString(st) + "\r");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            SshdPlugin.instance.getLogger().log(Level.WARNING, "Could not open " + f + ": File does not exist.");
            // Not showing the SSH motd is not a fatal failure, let the session continue.
        }

        // Doesn't really guarantee our actual system hostname but
        // it's better than not having one at all.
        cr.println("Connected to: " + this.GetHostname() + " (" + Bukkit.getServer().getName() + ")\r");
        cr.println(ConsoleLogFormatter.ColorizeString(Bukkit.getServer().getMotd()).replaceAll("\n", "\r\n"));
        cr.println("\r");
        cr.println("Type 'exit' to exit the shell." + "\r");
        cr.println("===============================================" + "\r");
    }
}
