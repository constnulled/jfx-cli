package dev.joyel.0xcafe6.engine;

import org.jd.core.v1.api.loader.Loader;

import dev.joyel.0xcafe6.options.DecompilerOptions;

public interface DecompilerEngine {

    String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception;
}
