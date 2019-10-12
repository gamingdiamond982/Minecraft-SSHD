package com.ryanmichela.sshd;

import com.ryanmichela.sshd.utils.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
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
import java.util.Collections;
import java.util.logging.Level;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

@Plugin(id = "spongesshd", name = "Sponge-SSHD", version = "1.3.7", description = "Sponge port for Minecraft-SSHD. SSH for your minecraft server!")
public class SshdPlugin
{
    String ListenAddress = "";
    Integer Port = 1025;
    String Mode = "";
    Boolean EnableSFTP = true;
    Integer LoginRetries = 3;
    String PasswordType = "";
    // Credentials

    private File modConfigFolder;
    CommandSpec MkpasswdCommand = CommandSpec.builder()
            .description(Text.of("Make a SSHD password hash"))
            .permission("sshd.mkpasswd")
            .build();

    public File getDataFolder()
    {
        return modConfigFolder;
    }

    private SshServer sshd;
	public static SshdPlugin instance;

	@Inject
	public Logger logger;

	public Config config;
	@Listener
	public void onServerStart(GameStartedServerEvent event)
	{
        instance = this;
        // Parse our config
        config = new Config();
        config.setup();

        // Now include it in our dealio here
        this.Mode = config.configNode.getNode("Mode").getString();
        this.PasswordType = config.configNode.getNode("PasswordType").getString();
        this.ListenAddress = config.configNode.getNode("ListenAddress").getString();
        this.Port = config.configNode.getNode("Port").getInt();
        this.LoginRetries = config.configNode.getNode("LoginRetries").getInt();
        this.EnableSFTP = config.configNode.getNode("EnableSFTP").getBoolean();


		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(this.Port);
		String host = this.ListenAddress;
		sshd.setHost(host.equals("all") ? null : host);

		File hostKey = new File(getDataFolder(), "hostkey");
		File authorizedKeys = new File(getDataFolder(), "authorized_keys");

		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostKey.toPath()));
		sshd.setShellFactory(new ConsoleShellFactory());
		sshd.setPasswordAuthenticator(new ConfigPasswordAuthenticator());
		sshd.setPublickeyAuthenticator(new PublicKeyAuthenticator(authorizedKeys));



		if (this.EnableSFTP)
		{
			sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
			sshd.setFileSystemFactory(
			        new VirtualFileSystemFactory(FileSystems.getDefault().getPath(getDataFolder().getAbsolutePath()).getParent().getParent()));
		}

        Sponge.getCommandManager().register(instance, MkpasswdCommand, "mkpasswd");
		//this.getCommand("mkpasswd").setExecutor(new MkpasswdCommand());

		sshd.setCommandFactory(new ConsoleCommandFactory());
		try
		{
			sshd.start();
		}
		catch (IOException e)
		{
			getLogger().error("Failed to start SSH server! ", e);
		}

		logger.info("Successfully running ExamplePlugin!!!");
	}

	public Logger getLogger()
	{
		return logger;
	}
}
