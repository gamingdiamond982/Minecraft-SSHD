package com.ryanmichela.sshd;

/**
 * Copyright 2013 Ryan Michela
 */

import jline.console.completer.Completer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ConsoleCommandCompleter implements Completer 
{
    private SpongeExecutorService MinecraftExecutor;

    public ConsoleCommandCompleter()
    {
        super();
        this.MinecraftExecutor = Sponge.getScheduler().createSyncExecutor(SshdPlugin.GetInstance());
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) 
    {
        Waitable<List<String>> waitable = new Waitable<List<String>>() 
        {
            @Override
            protected List<String> evaluate() 
            {
                return Sponge.getCommandManager().getSuggestions(Sponge.getServer().getConsole(), buffer, null);
            }
        };

        this.MinecraftExecutor.execute(waitable);
        try 
        {
            List<String> offers = waitable.get();
            if (offers == null) 
            {
                return cursor;
            }
            candidates.addAll(offers);

            final int lastSpace = buffer.lastIndexOf(' ');
            if (lastSpace == -1) 
            {
                return cursor - buffer.length();
            }
            else 
            {
                return cursor - (buffer.length() - lastSpace - 1);
            }
        } 
        catch (ExecutionException e) 
        {
            SshdPlugin.GetInstance().logger.warn("Unhandled exception when tab completing", e);
        } 
        catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
        }
        return cursor;
    }
}

