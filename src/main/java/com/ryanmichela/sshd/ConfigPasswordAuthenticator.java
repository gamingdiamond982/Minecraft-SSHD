package com.ryanmichela.sshd;

import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import com.ryanmichela.sshd.Cryptography;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2013 Ryan Michela
 */
public class ConfigPasswordAuthenticator implements PasswordAuthenticator {

	private Map<String, Integer> FailCounts = new HashMap<String, Integer>();

	@Override
	public boolean authenticate(String username, String password, ServerSession ss)
	{
		// Depending on our hash type, we have to try and figure out what we're doing.
		String HashType = SshdPlugin.GetInstance().PasswordType;
		String ConfigHash = SshdPlugin.GetInstance().config.configNode.getNode("Credentials", username.trim(), "password").getString();

		if (ConfigHash == null)
			SshdPlugin.GetLogger().warn("Config has no such user: " + username);
		else
		{
			try
			{
				if (HashType.equalsIgnoreCase("PLAIN"))
				{
					if (ConfigHash.equals(password))
					{
						FailCounts.put(username, 0);
						return true;
					}
				}
				else if (HashType.equalsIgnoreCase("bcrypt"))
				{
					if (Cryptography.BCrypt_ValidatePassword(password, ConfigHash))
					{
						FailCounts.put(username, 0);
						return true;
					}
				}
				else if (HashType.equalsIgnoreCase("pbkdf2"))
				{
					if (Cryptography.PBKDF2_ValidateHash(password, ConfigHash))
					{
						FailCounts.put(username, 0);
						return true;
					}
				}
				else if (HashType.equalsIgnoreCase("sha256"))
				{
					if (Cryptography.SHA256_ValidatePassword(password, ConfigHash))
					{
						FailCounts.put(username, 0);
						return true;
					}
				}
			}
			catch (Exception e)
			{
				// report it to the console.
				e.printStackTrace();
				// But continue as though there was a password failure.
			}
		}

		SshdPlugin.GetLogger().info("Failed login for " + username + " using " + HashType + "-based password authentication.");
		Integer tries = SshdPlugin.GetInstance().LoginRetries;

		try
		{
			Thread.sleep(3000);
			if (this.FailCounts.containsKey(username))
				this.FailCounts.put(username, this.FailCounts.get(username) + 1);
			else
				this.FailCounts.put(username, 1);

			if (this.FailCounts.get(username) >= tries)
			{
				this.FailCounts.put(username, 0);
				SshdPlugin.GetLogger().info("Too many failures for " + username + ", disconnecting.");
				ss.close(true);
			}
		}
		catch (InterruptedException e)
		{
			// do nothing
		}
		return false;
	}
}
