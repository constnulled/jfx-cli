package dev.joyel.0xcafe6.jd;

import org.jd.core.v1.api.loader.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.joyel.0xcafe6.engine.DecompilerEngine;
import dev.joyel.0xcafe6.options.DecompilerOptions;

/**
 * Decompiler implementation, which uses an JD Core to decompile classes.
 */
public class JavaDecompiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaDecompiler.class);

    private final DecompilerOptions options;
    private final DecompilerEngine engine;

    public JavaDecompiler(DecompilerOptions options) {
        this.options = options;
        this.engine = options.getDecompilerType().createEngine();
    }

    public String decompileClass(Loader loader, String internalName) {
        LOGGER.debug("Decompiling class {}", internalName);
        if (loader == null) {
            LOGGER.warn("Loader was null");
            return null;
        }

        try {
            return engine.decompile(loader, internalName, options);
        } catch (Exception e) {
            LOGGER.error("Can't decompile " + internalName, e);
            return null;
        }
    }

    public DecompilerOptions getOptions() {
        return options;
    }
}
