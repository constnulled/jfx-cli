/*******************************************************************************
 * Copyright (C) 2015 Josef Cacek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package dev.joyel.0xcafe6.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.joyel.0xcafe6.IOUtils;
import dev.joyel.0xcafe6.JavaDecompiler;
import dev.joyel.0xcafe6.loader.CachedLoader;
import dev.joyel.0xcafe6.options.DecompilerOptions;
import dev.joyel.0xcafe6.output.JDOutput;

/**
 * Input plugin for ZIP files (e.g. jar, war, ...)
 *
 * @author Josef Cacek
 */
public class ZipFileInput extends AbstractFileJDInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFileInput.class);

    /**
     * Constructor.
     */
    public ZipFileInput(String path) {
        super(path);
    }

    /**
     * Constructor.
     */
    public ZipFileInput(String filePath, String pattern) throws IllegalArgumentException {
        super(filePath, pattern);
    }

    /**
     * Parses all entres in the zip and decompiles it writing results to {@link JDOutput} instance.
     *
     * @see dev.joyel.0xcafe6.input.JDInput#decompile(dev.joyel.0xcafe6.JavaDecompiler, dev.joyel.0xcafe6.output.JDOutput)
     */
    @Override
    public void decompile(JavaDecompiler javaDecompiler, JDOutput jdOutput) {
        if (javaDecompiler == null || jdOutput == null) {
            LOGGER.warn("Decompiler or JDOutput are null");
            return;
        }

        DecompilerOptions options = javaDecompiler.getOptions();
        boolean skipResources = options.isSkipResources();

        LOGGER.debug("Initializing decompilation of a zip file {}", file);

        jdOutput.init(options, file.getPath());
        ZipInputStream zis = null;
        CachedLoader cachedLoader = new CachedLoader();
        try {
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = null;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    final String entryName = entry.getName();
                    if (skipThePath(entryName)) {
                        continue;
                    }
                    if (IOUtils.isClassFile(entryName)) {
                        LOGGER.debug("Caching {}", entryName);
                        try {
                            cachedLoader.addClass(entryName, zis);
                        } catch (IOException e) {
                            LOGGER.error("IOException occured", e);
                        }
                    } else if (!skipResources) {
                        LOGGER.debug("Processing resource file {}", entryName);
                        jdOutput.processResource(entryName, zis);
                    } else {
                        LOGGER.trace("Skipping resource file {}", entryName);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("IOException occured", e);
        } finally {
            IOUtils.closeQuietly(zis);
        }
        Stream<String> classNamesStream = options.isParallelProcessingAllowed() ? cachedLoader.getClassNames().parallelStream()
                : cachedLoader.getClassNames().stream();
        classNamesStream.filter(s -> !IOUtils.isInnerClass(s)).map(s -> IOUtils.cutClassSuffix(s)).forEach(name -> {
            try {
                jdOutput.processClass(name, javaDecompiler.decompileClass(cachedLoader, name));
            } catch (Exception e) {
                LOGGER.error("Exception when decompiling class " + name, e);
            }
        });
        jdOutput.commit();
    }
}
