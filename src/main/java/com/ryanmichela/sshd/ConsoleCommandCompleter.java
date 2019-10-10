package com.ryanmichela.sshd;

/**
 * Copyright 2013 Ryan Michela
 */
import net.md_5.bungee.api.plugin.Command;

import jline.console.completer.Completer;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ConsoleCommandCompleter implements Completer 
{
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) 
    {
        Waitable<List<String>> waitable = new Waitable<List<String>>() 
        {
            @Override
            protected List<String> evaluate() 
            {
                List<String> tabcomplete = new ArrayList<String>();
                for (Map.Entry<String, Command> map : SshdPlugin.instance.getProxy().getPluginManager().getCommands())
                {
                    String cmd = map.getKey();
                    Command value = map.getValue();

                    if (cmd.startsWith(buffer))
                        tabcomplete.add(cmd);
                }

                return tabcomplete;
            }
        };

        SshdPlugin.instance.getProxy().getScheduler().runAsync(SshdPlugin.instance, waitable);
        try 
        {
            List<String> offers = waitable.get();
            if (offers == null) 
                return cursor;

            candidates.addAll(offers);

            final int lastSpace = buffer.lastIndexOf(' ');
            if (lastSpace == -1) 
                return cursor - buffer.length();
            else
                return cursor - (buffer.length() - lastSpace - 1);
        } 
        catch (ExecutionException e) 
        {
            SshdPlugin.instance.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", e);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return cursor;
    }
}

