package com.ryanmichela.sshd;

import java.util.Arrays;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

import com.ryanmichela.sshd.Cryptography;
import com.ryanmichela.sshd.SshdPlugin;

public class MkpasswdCommand extends Command
{
	public MkpasswdCommand()
	{
		super("mkpasswd");
	}

	public void SendSyntax(CommandSender sender, boolean invalid)
	{
		if (invalid)
			sender.sendMessage(new ComponentBuilder("Invalid Syntax").color(ChatColor.RED).create());
		sender.sendMessage(new ComponentBuilder("/mkpasswd <help|hash> <password>").color(ChatColor.GREEN).create());
		sender.sendMessage(new ComponentBuilder("Supported Hashes: SHA256, PBKDF2, BCRYPT, PLAIN").color(ChatColor.BLUE).create());
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		String algoritm, password;
		try
		{
			// Stupid bukkit, we have to concatenate the arguments together if they're using
			// spaces in their passwords otherwise it won't be as strong as it should be.
			algoritm = args[0];
			password = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			if (password.length() == 0)
				throw new ArrayIndexOutOfBoundsException(); // shortcut 
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			this.SendSyntax(sender, false);
			return;
		}

		// If they're a player, check and make sure they have a permission
		// If they're not a player (aka, the console), just return true.
		boolean hasperm = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer)sender).hasPermission("sshd.mkpasswd") : true;

		if (hasperm)
		{ 
			try
			{
				String hash = "";
				// Dumb but whatever. Some people are really dense.
				if (algoritm.equalsIgnoreCase("PLAIN"))
				{
					// I mean c'mon...
					sender.sendMessage("Bro really? it's literally your unencrypted password...");
					return;
				}
				else if (algoritm.equalsIgnoreCase("pbkdf2"))
					hash = Cryptography.PBKDF2_HashPassword(password);
				else if (algoritm.equalsIgnoreCase("bcrypt"))
					hash = Cryptography.BCrypt_HashPassword(password);
				else if (algoritm.equalsIgnoreCase("sha256"))
					hash = Cryptography.SHA256_HashPassword(password);
				else
				{
					this.SendSyntax(sender, !algoritm.equalsIgnoreCase("help"));
					return;
				}

				sender.sendMessage(new ComponentBuilder("Your Hash: " + hash).color(ChatColor.BLUE).create());
			}
			catch (Exception e)
			{
				// We're console, just print the stack trace.
				e.printStackTrace();
			}
		}
	}
}