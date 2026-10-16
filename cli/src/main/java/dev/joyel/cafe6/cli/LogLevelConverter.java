package dev.joyel.cafe6.cli;

import ch.qos.logback.classic.Level;

import com.beust.jcommander.IStringConverter;

/**
 * Logback Level type converter for JCommander.
 */
public class LogLevelConverter implements IStringConverter<Level> {

    @Override
    public Level convert(String value) {
        return Level.toLevel(value, Level.INFO);
    }

}
