package com.github.kwart.jd.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.Level;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.github.kwart.jd.engine.DecompilerType;
import com.github.kwart.jd.options.DecompilerOptions;

/**
 * Command line arguments for jd-cli.
 */
public class CLIArguments implements DecompilerOptions {

    @Parameter(description = "[Files to decompile]")
    private final List<String> files = new ArrayList<String>();

    @Parameter(names = { "--help", "-h" }, description = "shows this help", help = true)
    private boolean help;

    @Parameter(names = { "--version", "-v" }, description = "shows the version", help = true)
    private boolean version;

    @Parameter(names = { "--outputZipFile", "-oz" },
            description = "takes a [zipFilePath] as a parameter and configures ZIP output for this path",
            converter = FileConverter.class)
    private File zipOutFile;

    @Parameter(names = { "--outputDir", "-od" },
            description = "takes a [directoryPath] as a parameter and configures a flat DIR output for this path",
            converter = FileConverter.class)
    private File dirOutFile;

    @Parameter(names = { "--outputDirStructured", "-ods" },
            description = "takes a [directoryPath] as a parameter and configures a structured DIR output for this path",
            converter = FileConverter.class)
    private File dirOutFileStructured;

    @Parameter(names = { "--outputConsole", "-oc" }, description = "enables output to system output stream")
    private boolean consoleOut;

    @Parameter(names = { "--skipResources", "-sr" }, description = "skips processing resources")
    private boolean skipResources;

    @Parameter(names = { "--displayLineNumbers", "-n" }, description = "displays line numbers in decompiled classes")
    private boolean displayLineNumbers;

    @Parameter(names = { "--escapeUnicodeCharacters", "-eu" }, description = "escape unicode characters in decompiled classes")
    private boolean escapeUnicodeCharacters;

    @Parameter(names = { "--serialProcessing", "-sp" }, description = "don't use parallel processing")
    private boolean serialProcessing;

    @Parameter(names = { "--logLevel", "-g" },
            description = "takes [level] as parameter and sets it as the CLI log level."
                    + " Possible values are: ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF",
            converter = LogLevelConverter.class)
    private final Level logLevel = Level.INFO;

    @Parameter(names = { "--pattern", "-p" }, description =
            "RegExp pattern which the to-be-decompiled file has to match. Not matching entries are skipped.")
    private final String pattern = null;

    @Parameter(names = { "--decompiler", "-dc" },
            description = "decompiler engine to use. Possible values are: jd, cfr, fernflower, procyon",
            converter = DecompilerTypeConverter.class)
    private DecompilerType decompilerType = DecompilerType.JD;

    public List<String> getFiles() {
        return files;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isVersion() {
        return version;
    }

    public File getZipOutFile() {
        return zipOutFile;
    }

    public File getDirOutFile() {
        return dirOutFile;
    }

    public File getDirOutFileStructured() {
        return dirOutFileStructured;
    }

    public boolean isConsoleOut() {
        return consoleOut;
    }

    public boolean isSkipResources() {
        return skipResources;
    }

    public boolean isDisplayLineNumbers() {
        return displayLineNumbers;
    }

    public boolean isEscapeUnicodeCharacters() {
        return escapeUnicodeCharacters;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public DecompilerType getDecompilerType() {
        return decompilerType;
    }

    public boolean isOutputPluginSpecified() {
        return consoleOut || zipOutFile != null || dirOutFile != null || dirOutFileStructured != null;
    }

    @Override
    public boolean isParallelProcessingAllowed() {
        return ! serialProcessing;
    }
}
