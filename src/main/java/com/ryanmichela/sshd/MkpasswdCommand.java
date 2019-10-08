package com.ryanmichela.sshd;

import java.util.Arrays;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import com.ryanmichela.sshd.Cryptography;
import com.ryanmichela.sshd.SshdPlugin;

public class MkpasswdCommand extends Command
{

	public MkpasswdCommand()
	{
		super("mkpasswd");
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		ProxiedPlayer player = (ProxiedPlayer) sender;

		String algoritm, password;
		algoritm = args[0];
		password = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		try
		{
			// Stupid bukkit, we have to concatenate the arguments together if they're using
			// spaces in their passwords otherwise it won't be as strong as it should be.

		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// ignore it.
		}

		// If they're console, allow regardless.
		try 
		{
			if (player.hasPermission("sshd.mkpasswd"))
			{
				// Dumb but whatever. Some people are really dense.
				if (algoritm.equalsIgnoreCase("PLAIN"))
					sender.sendMessage(password);
				else if (algoritm.equalsIgnoreCase("pbkdf2"))
					sender.sendMessage(Cryptography.PBKDF2_HashPassword(password));
				else if (algoritm.equalsIgnoreCase("bcrypt"))
					sender.sendMessage(Cryptography.BCrypt_HashPassword(password));
				else if (algoritm.equalsIgnoreCase("sha256"))
					sender.sendMessage(Cryptography.SHA256_HashPassword(password));
			}
		}
		catch (Exception e)
		{
			// since this is a player, send a failure message
			sender.sendMessage("An error occured, please check console.");
			e.printStackTrace();
		}
	}
}

/*
class MkpasswdCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		String algoritm, password;
		try
		{
			// Stupid bukkit, we have to concatenate the arguments together if they're using
			// spaces in their passwords otherwise it won't be as strong as it should be.
			algoritm = args[0];
			password = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// ignore it.
			return false;
		}

		// If they're console, allow regardless.
        if (!(sender instanceof Player))
		{
			if (label.equalsIgnoreCase("mkpasswd"))
			{
				try
				{
                    // Dumb but whatever. Some people are really dense.
					if (algoritm.equalsIgnoreCase("PLAIN"))
					{
						// I mean c'mon...
						sender.sendMessage("Bro really? it's literally your unencrypted password...");
					}
                    else if (algoritm.equalsIgnoreCase("pbkdf2"))
						sender.sendMessage("Your hash: " + Cryptography.PBKDF2_HashPassword(password));
					else if (algoritm.equalsIgnoreCase("bcrypt"))
						sender.sendMessage("Your hash: " + Cryptography.BCrypt_HashPassword(password));
					else if (algoritm.equalsIgnoreCase("sha256"))
						sender.sendMessage("Your hash: " + Cryptography.SHA256_HashPassword(password));
					else if (algoritm.equalsIgnoreCase("help"))
						sender.sendMessage("Supported hash algorithms: pbkdf2, bcrypt, sha256, plain");
					else
						return false;
				}
				catch (Exception e)
				{
					// We're console, just print the stack trace.
					e.printStackTrace();
					return false;
				}
				return true;
			}
		}
        else
        {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("mkpasswd"))
			{
				try 
				{
					if (player.hasPermission("sshd.mkpasswd"))
					{
						// Dumb but whatever. Some people are really dense.
						if (algoritm.equalsIgnoreCase("PLAIN"))
							sender.sendMessage(password);
						else if (algoritm.equalsIgnoreCase("pbkdf2"))
							sender.sendMessage(Cryptography.PBKDF2_HashPassword(password));
						else if (algoritm.equalsIgnoreCase("bcrypt"))
							sender.sendMessage(Cryptography.BCrypt_HashPassword(password));
						else if (algoritm.equalsIgnoreCase("sha256"))
							sender.sendMessage(Cryptography.SHA256_HashPassword(password));
						else
							return false;
					}
				}
				catch (Exception e)
				{
					// since this is a player, send a failure message
					sender.sendMessage("An error occured, please check console.");
					e.printStackTrace();
					return false;
				}
				return true;
            }
		}
		return false;
    }
}*/