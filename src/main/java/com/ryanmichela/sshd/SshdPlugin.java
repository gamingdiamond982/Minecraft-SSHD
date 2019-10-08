package com.ryanmichela.sshd;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.config.*;

import com.ryanmichela.sshd.ConsoleShellFactory;
import com.ryanmichela.sshd.MkpasswdCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.logging.Level;

/**
 * Copyright 2013 Ryan Michela
 */
public final class SshdPlugin extends Plugin
{
  private SshServer sshd;
  public static SshdPlugin instance;
  private File file;
  public Configuration configuration;


	@Override public void onLoad()
	{
		file = new File(ProxyServer.getInstance().getPluginsFolder()+ "/config.yml");

		File authorizedKeys = new File(getDataFolder(), "authorized_keys");
		if (!authorizedKeys.exists())
			authorizedKeys.mkdirs();

		try
		{
			File motd = new File(getDataFolder(), "motd.txt");
			if (!motd.exists())
			{
				InputStream link = (getClass().getResourceAsStream("/motd.txt"));
				Files.copy(link, motd.getAbsoluteFile().toPath());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}


		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

			// more testing
			configuration.set("test", "This configuration file works!");
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Don't go any lower than INFO or SSHD will cause a stack overflow exception.
		// SSHD will log that it wrote bites to the output stream, which writes
		// bytes to the output stream - ad nauseaum.
		getLogger().setLevel(Level.INFO);

		// config testing
		String printout = configuration.getString("test");
		getLogger().info(printout);
	}

	@Override public void onEnable()
	{
		instance = this;

		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(configuration.getInt("Port", 1025));
		String host = configuration.getString("ListenAddress", "all");
		sshd.setHost(host.equals("all") ? null : host);

		File hostKey		= new File(getDataFolder(), "hostkey");
		File authorizedKeys = new File(getDataFolder(), "authorized_keys");

		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostKey.toPath()));
		sshd.setShellFactory(new ConsoleShellFactory());
		sshd.setPasswordAuthenticator(new ConfigPasswordAuthenticator());
		sshd.setPublickeyAuthenticator(new PublicKeyAuthenticator(authorizedKeys));

		if (configuration.getBoolean("EnableSFTP"))
		{
			sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
			sshd.setFileSystemFactory(
				new VirtualFileSystemFactory(FileSystems.getDefault().getPath(getDataFolder().getAbsolutePath()).getParent().getParent()));
		}

		getProxy().getPluginManager().registerCommand(this, new MkpasswdCommand());
		//this.getCommand("mkpasswd").setExecutor(new MkpasswdCommand());

		sshd.setCommandFactory(new ConsoleCommandFactory());
		try
		{
			sshd.start();
		}
		catch (IOException e)
		{
			getLogger().log(Level.SEVERE, "Failed to start SSH server! ", e);
		}
	}

	@Override public void onDisable()
	{
		try
		{
			sshd.stop();
		}
		catch (Exception e)
		{
			// do nothing
		}
	}

	public static SshdPlugin getInstance() {
		return instance;
	}

	private static void setInstance(SshdPlugin instance) {
		SshdPlugin.instance = instance;
	}
}
