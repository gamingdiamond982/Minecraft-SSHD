package com.ryanmichela.sshd.utils;

import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import com.google.inject.Inject;
import com.ryanmichela.sshd.SshdPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.IOException;

public class Config
{
	// Give us a config!
	@Inject
	@DefaultConfig(sharedRoot = false)
	// idk what to do with this one.
	private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(SshdPlugin.GetInstance().DefaultConfig).build();

	public CommentedConfigurationNode configNode;

	public void setup()
	{
		// I'm not sure if this will even work, the sponge config API is confusing.
		if (!Files.exists(SshdPlugin.GetInstance().DefaultConfig))
		{
			try
			{
				Files.createFile(SshdPlugin.GetInstance().DefaultConfig);
				this.load();
				this.populate();
				this.save();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
			this.load();
	}

	public void load()
	{
		try
		{
			configNode = this.configLoader.load();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void save()
	{
		try
		{
			this.configLoader.save(this.configNode);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void populate()
	{
		this.configNode.getNode("ListenAddress").setValue("all").setComment
				("The IP addresses(s) the SSH server will listen on. Use a comma separated list for multiple addresses.\n" +
				"Leave as \"all\" for all addresses.");
		this.configNode.getNode("Port").setValue("1025").setComment(
				"# The port the SSH server will listen on. Note that anything above 1024 will require you to run\n" +
				"# the whole minecraft server with elevated privileges, this is not recommended and you should\n" +
				"# use iptables to route packets from a lower port.");
		this.configNode.getNode("Mode").setValue("DEFAULT").setComment("Operational mode. Don't touch if you don't know what you're doing. Can be either DEFAULT or RPC");
		this.configNode.getNode("EnableSFTP").setValue("true").setComment(
				"# Enable built-in SFTP server or not. You'll be able to connect and upload/download files via SFTP protocol.\n" +
				"# Might be useful for testing purposes as well , i. e. docker containers.");
		this.configNode.getNode("LoginRetries").setValue("3").setComment(
				"# Number of times a person can fail to use an SSH key or enter a password\n" +
				"# before it terminates the connection.");

		this.configNode.getNode("PasswordType").setValue("bcrypt").setComment
				("########################################################################################\n" +
				"# By default, only public key authentication is enabled. This is the most secure mode.\n" +
				"# To authorize a user to login with their public key, install their key using the\n" +
				"# OpenSSH authorized_keys file format in the authorized_users directory. Name the key\n" +
				"# file with the user's username and no extension. Note: If you want to let a user have\n" +
				"# many keys, you can append the keys to their file in authorized_users.\n" +
				"########################################################################################\n" +
				"For less secure username and password based authentication, complete the sections below.\n" +
				"\n" +
				"# Type of hashing to use for the passwords below.\n" +
				"# Options are: PLAIN (insecure), bcrypt, pbkdf2, sha256\n" +
				"#\n" +
				"# You can use the console/in-game command `/mkpasswd [hash] PASSWORD` to\n" +
				"# generate a password hash string then copy it for your passwords below.\n" +
				"# You can also use `/mkpasswd help` to see what algorithms are supported.");

		this.configNode.getNode("Credentials").setComment("# Associate each username with a password hash (or the password if the PasswordType is set to PLAIN)");
		this.configNode.getNode("Credentials", "user1", "password").setValue("MySecretPassword");
		this.configNode.getNode("Credentials", "user2", "password").setValue("MyBestFriendsPassword");
		//this.configNode.getNode("").setValue("").setComment("");


		/*
		this.Mode = config.configNode.getNode("Mode").getString();
        this.PasswordType = config.configNode.getNode("PasswordType").getString();
        this.ListenAddress = config.configNode.getNode("ListenAddress").getString();
        this.Port = config.configNode.getNode("Port").getInt();
        this.LoginRetries = config.configNode.getNode("LoginRetries").getInt();
        this.EnableSFTP = config.configNode.getNode("EnableSFTP").getBoolean();

		this.configNode.getNode("mysql").setComment("MySQL database for Whitelisting");
		this.configNode.getNode("mysql", "port").setValue(3306).setComment("MySQL server port");
		this.configNode.getNode("mysql", "host").setValue("localhost").setComment("MySQL server to connect to");
		this.configNode.getNode("mysql", "database").setValue("WhitelistSync").setComment("MySQL database for Whitelisting");
		this.configNode.getNode("mysql", "username").setValue("Whitelist").setComment("MySQL username for the database");
		this.configNode.getNode("mysql", "password").setValue("letmein").setComment("MySQL password for the database");
		*/
	}
}
