package com.github.kwart.jd.engine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jd.core.v1.api.loader.Loader;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kwart.jd.options.DecompilerOptions;

final class VineflowerEngine implements DecompilerEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(VineflowerEngine.class);

    @Override
    public String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception {
        byte[] mainBytes = ClassBytes.require(loader, internalName);
        String className = ClassBytes.thisClassName(mainBytes);
        Path work = Files.createTempDirectory("jd-cli-vf-");
        try {
            Path in = Files.createDirectory(work.resolve("in"));
            Path out = Files.createDirectory(work.resolve("out"));
            for (Map.Entry<String, byte[]> entry : ClassBytes.withInnerClasses(loader, className, mainBytes).entrySet()) {
                Path target = in.resolve(entry.getKey() + ".class");
                Files.createDirectories(target.getParent());
                Files.write(target, entry.getValue());
            }
            Map<String, Object> vfOptions = new HashMap<>();
            vfOptions.put("asc", options.isEscapeUnicodeCharacters() ? "1" : "0");
            Runner runner = new Runner(out.toFile(), vfOptions);
            runner.addSource(in.toFile());
            runner.decompileContext();
            return readResult(out, className.substring(className.lastIndexOf('/') + 1) + ".java");
        } finally {
            deleteRecursively(work);
        }
    }

    private static String readResult(Path out, String fileName) throws IOException {
        try (Stream<Path> files = Files.walk(out)) {
            Optional<Path> found = files.filter(p -> p.getFileName().toString().equals(fileName)).findFirst();
            if (!found.isPresent()) {
                throw new IOException("Decompiler produced no output for " + fileName);
            }
            return new String(Files.readAllBytes(found.get()), StandardCharsets.UTF_8);
        }
    }

    private static void deleteRecursively(Path dir) {
        try (Stream<Path> files = Files.walk(dir)) {
            files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException e) {
            LOGGER.debug("Unable to clean temporary directory {}", dir, e);
        }
    }

    private static final class Runner extends ConsoleDecompiler {

        Runner(File destination, Map<String, Object> options) {
            super(destination, options, new QuietLogger());
        }
    }

    private static final class QuietLogger extends IFernflowerLogger {

        @Override
        public void writeMessage(String message, Severity severity) {
            LOGGER.debug("{}: {}", severity, message);
        }

        @Override
        public void writeMessage(String message, Severity severity, Throwable t) {
            LOGGER.debug("{}: {}", severity, message, t);
        }
    }
}
