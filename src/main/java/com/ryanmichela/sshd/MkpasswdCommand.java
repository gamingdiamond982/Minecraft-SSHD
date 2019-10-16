package com.ryanmichela.sshd;

/*
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
*/
import java.util.Arrays;

import com.ryanmichela.sshd.Cryptography;
import com.ryanmichela.sshd.SshdPlugin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.text.Text;

public class MkpasswdCommand implements CommandExecutor
{
	private static CommandSpec cmdspec;
	public static void BuildCommand()
	{
		CommandSpec pbkdf2 = CommandSpec.builder()
										.description(Text.of("PBKDF2 hashed password"))
										.permission("sshd.mkpasswd.pbkdf2")
										.arguments(GenericArguments.remainingJoinedStrings(Text.of("password")))
										.executor((CommandSource source, CommandContext args) -> {
											try
											{
												source.sendMessage(Text.of("\u00A79Your Hash: " + Cryptography.PBKDF2_HashPassword(args.<String>getOne("password").get())));
											}
											catch (Exception e)
											{
												e.printStackTrace();
												return null;
											}
											return CommandResult.success();
										})
										.build();

		CommandSpec bcrypt = CommandSpec.builder()
										.description(Text.of("BCrypt hashed password"))
										.permission("sshd.mkpasswd.bcrypt")
										.arguments(GenericArguments.remainingJoinedStrings(Text.of("password")))
										.executor((CommandSource source, CommandContext args) -> {
											try
											{
												source.sendMessage(Text.of("\u00A79Your Hash: " + Cryptography.BCrypt_HashPassword(args.<String>getOne("password").get())));
											}
											catch (Exception e)
											{
												e.printStackTrace();
												return null;
											}
											return CommandResult.success();
										})
										.build();

		CommandSpec sha256 = CommandSpec.builder()
										.description(Text.of("SHA256 hashed password"))
										.permission("sshd.mkpasswd.sha256")
										.arguments(GenericArguments.remainingJoinedStrings(Text.of("password")))
										.executor((CommandSource source, CommandContext args) -> {
											try
											{
												source.sendMessage(Text.of("\u00A79Your Hash: " + Cryptography.SHA256_HashPassword(args.<String>getOne("password").get())));
											}
											catch (Exception e)
											{
												e.printStackTrace();
												return null;
											}
											return CommandResult.success();
										})
										.build();

		// The plain text "encryption" method
		CommandSpec plain = CommandSpec.builder()
										.description(Text.of("Plain text password (insecure)"))
										.permission("sshd.mkpasswd.plain")
										.arguments(GenericArguments.remainingJoinedStrings(Text.of("password")))
										.executor((CommandSource source, CommandContext args) -> {
											source.sendMessage(Text.of("\u00A79Your Hash: \u00A7cIt's literally your unhashed password."));
											return CommandResult.success();
										})
										.build();

		// the root "mkpasswd" command
		cmdspec = CommandSpec.builder()
							 .description(Text.of("Create an SSHd password using hashes"))
							 .extendedDescription(Text.of("Supported Hashes: SHA256, PBKDF2, BCRYPT, PLAIN"))
							 .permission("sshd.mkpasswd")
							 .child(plain, "plain")
							 .child(sha256, "sha256")
							 .child(bcrypt, "bcrypt")
							 .child(pbkdf2, "pbkdf2")
							 .executor(new MkpasswdCommand())
							 .build();

		// Register our command with Sponge.
		Sponge.getCommandManager().register(SshdPlugin.GetInstance(), cmdspec, "mkpasswd");
	}

	// so sponge needed this, still figuring out the sponge API ~ Zach
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
	{
		// This command doesn't do anything.
		src.sendMessage(Text.of("\u00A7a/mkpasswd <hash> <password...>\u00A7r"));
		src.sendMessage(Text.of("\u00A79Supported Hashes: SHA256, PBKDF2, BCRYPT, PLAIN\u00A7r"));
		return CommandResult.success();
	}
}
