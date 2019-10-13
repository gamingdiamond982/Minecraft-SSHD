package com.ryanmichela.sshd;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

import jline.console.ConsoleReader;
import org.apache.sshd.common.SshException;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.nio.charset.Charset;

/**
 * Copyright 2014 Ryan Michela
 */
public class StreamHandlerAppender implements Appender 
{
    private ConsoleReader console;
    private UUID uuid;
    private PatternLayout MinecraftLayout = PatternLayout.newBuilder().withPattern("%highlightError{[%d{HH:mm:ss} %level] [%logger]: %minecraftFormatting{%msg}%xEx}").build();
    private PatternLayout MojangLayout = PatternLayout.newBuilder().withPattern("%highlightError{[%d{HH:mm:ss} %level]: %minecraftFormatting{%msg}%xEx}").build();

    public StreamHandlerAppender(ConsoleReader console) 
    {
        this.console = console;
        uuid = UUID.randomUUID();
    }

    @Override
    public void append(LogEvent logEvent) 
    {
        if (logEvent.getLevel() == Level.DEBUG || logEvent.getLevel() == Level.TRACE)
            return;
        
        try 
        {
            // Delete the jline's `> ` character
            this.console.print(ConsoleReader.BACKSPACE + "" + ConsoleReader.BACKSPACE);
            // Print our message
            if (logEvent.getLoggerName().matches("net\\.minecraft\\..*|com\\.mojang\\..*"))
                this.console.println(ConsoleLogFormatter.ColorizeString(this.MojangLayout.toSerializable(logEvent)).replaceAll("\n", "\r\n") + "\r");
            else
                this.console.println(ConsoleLogFormatter.ColorizeString(this.MinecraftLayout.toSerializable(logEvent)).replaceAll("\n", "\r\n") + "\r");
            // Reset the console (colors, formatting, etc)
            this.console.print(ConsoleReader.RESET_LINE + "");
            try 
            {
                // Attempt to draw new console line
                this.console.drawLine();
            } 
            catch (Throwable ex) 
            {
                this.console.getCursorBuffer().clear();
            }
            // Push it to the end user.
            this.console.flush();
        } 
        catch (SshException ex) 
        {
            // do nothing
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "StreamHandlerAppender:" + uuid.toString();
    }

    @Override
    public Layout<? extends Serializable> getLayout() {
        return null;
    }

    @Override
    public boolean ignoreExceptions() {
        return false;
    }

    @Override
    public ErrorHandler getHandler() {
        return null;
    }

    @Override
    public void setHandler(ErrorHandler errorHandler) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
