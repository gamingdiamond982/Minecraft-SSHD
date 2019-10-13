package com.ryanmichela.sshd;

import com.ryanmichela.sshd.utils.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import com.ryanmichela.sshd.ConsoleShellFactory;
import com.ryanmichela.sshd.MkpasswdCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Level;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

@Plugin(
	id = "sshd", 
	name = "Minecraft-SSHD", 
	version = "1.3.7", 
	description = "Sponge port for Minecraft-SSHD. SSH for your minecraft server!", 
	authors={"Justin Crawford", "Zachery Coleman"}
)
public class SshdPlugin
{
    String ListenAddress = "";
    Integer Port = 1025;
    String Mode = "";
    Boolean EnableSFTP = true;
    Integer LoginRetries = 3;
    String PasswordType = "";
    // Credentials

    private SshServer sshd;
	private static SshdPlugin instance;

	@Inject
	public Logger logger;

	@Inject
    @DefaultConfig(sharedRoot = false)
	public Path DefaultConfig;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	public Path ConfigDir;

	public Config config;

	@Listener
	public void onServerStart(GameStartedServerEvent event)
	{
        instance = this;
        // Parse our config
        config = new Config();
		config.setup();

		// Make sure our authorized_keys folder exists
		File authorizedKeys = new File(this.ConfigDir.toFile(), "authorized_keys");
		if (!authorizedKeys.exists())
			authorizedKeys.mkdirs();

        // Now include it in our dealio here
        this.Mode = config.configNode.getNode("Mode").getString();
        this.PasswordType = config.configNode.getNode("PasswordType").getString();
        this.ListenAddress = config.configNode.getNode("ListenAddress").getString();
        this.Port = config.configNode.getNode("Port").getInt();
        this.LoginRetries = config.configNode.getNode("LoginRetries").getInt();
		this.EnableSFTP = config.configNode.getNode("EnableSFTP").getBoolean();

		try
		{
			File motd = new File(this.ConfigDir.toFile(), "motd.txt");
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


		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(this.Port);
		sshd.setHost(this.ListenAddress.equals("all") ? null : this.ListenAddress);

		File hostKey = new File(this.ConfigDir.toFile(), "hostkey");

		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostKey.toPath()));
		sshd.setShellFactory(new ConsoleShellFactory());
		sshd.setPasswordAuthenticator(new ConfigPasswordAuthenticator());
		sshd.setPublickeyAuthenticator(new PublicKeyAuthenticator(authorizedKeys));

		if (this.EnableSFTP)
		{
			sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
			sshd.setFileSystemFactory(new VirtualFileSystemFactory(this.ConfigDir.getParent().getParent()));
		}

		MkpasswdCommand.BuildCommand();

		sshd.setCommandFactory(new ConsoleCommandFactory());
		try
		{
			sshd.start();
		}
		catch (IOException e)
		{
			logger.error("Failed to start SSH server! ", e);
		}

		logger.info("Loaded Minecraft-SSHD.");
	}

	public static SshdPlugin GetInstance()
	{
		return instance;
	}

	public Logger GetLogger()
	{
		return this.logger;
	}
}
