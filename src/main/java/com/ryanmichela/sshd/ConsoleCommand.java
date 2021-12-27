package com.ryanmichela.sshd;

import com.ryanmichela.sshd.implementations.SSHDCommandSender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.StreamHandler;


public class ConsoleCommand implements Command, Runnable
{
    String command;
    InputStream in;
    OutputStream out;
    OutputStream err;
    ExitCallback callback;
    Environment environment;
    Thread thread;
    String Username;


    public jline.console.ConsoleReader ConsoleReader;
    public SSHDCommandSender SshdCommandSender;

    public ConsoleCommand() {}

    public InputStream getIn()
    {
        return in;
    }

    public ConsoleCommand(String command)
    {
        this.command = command;
    }


    public void setInputStream(InputStream in)
    {
        this.in = in;
    }

    public void setOutputStream(OutputStream out)
    {
        this.out = out;
    }

    public void setErrorStream(OutputStream err)
    {
        this.err = err;
    }

    public void setExitCallback(ExitCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public void start(ChannelSession cs, Environment env) throws IOException
    {
        try
        {
            String username = env.getEnv().get(Environment.ENV_USER);
            Optional<String> optcred = PermissionUtil.GetCredential(username, "console");
            // They don't have access.
            if (optcred.isPresent() && !optcred.get().contains("R"))
            {
                cs.close(true);
                return;
            }
            else
                SshdPlugin.instance.getLogger().warning("There is no $default pseudo-user under credential, allowing unrestricted access...");


            this.environment = env;
            this.Username = username;
            this.SshdCommandSender = new SSHDCommandSender();
            this.SshdCommandSender.console = this;
            thread = new Thread(this, "SSHD ConsoleShell " + username);
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException("Error starting shell", e);
        }

    }


    @Override
    public void destroy(ChannelSession cn) {}

    public Runnable run_command(CommandSender commandSender, String cmd) {

        return  () ->
                {
                    if (SshdPlugin.instance.getConfig().getString("Mode", "DEFAULT").equals("RPC") && cmd.startsWith("rpc"))
                    {
                        // NO ECHO NO PREAMBLE AND SHIT
                        String arg = cmd.substring("rpc".length() + 1, cmd.length());
                        Bukkit.dispatchCommand(commandSender, arg);
                    }
                    else
                    {
                        // Don't send our mkpasswd command output. This will echo passwords back
                        // to the console for all to see. This command is strictly between
                        // our plugin and the connected client.

                        if (!cmd.split(" ")[0].equals("mkpasswd"))
                        {
                            SshdPlugin.instance.getLogger().info("<" + this.Username + "> " + cmd);
                            Bukkit.dispatchCommand(commandSender, cmd);
                        }
                        else
                        {
                            Bukkit.dispatchCommand(commandSender, cmd);
                        }

                    }
                };
    }

    public void sendMessage(String message) {
        try {
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTask(SshdPlugin.instance, () -> {
            run_command(this.SshdCommandSender, command).run();
            callback.onExit(0);
        });
    }
}

