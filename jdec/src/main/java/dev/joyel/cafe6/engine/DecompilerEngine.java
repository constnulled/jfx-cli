package dev.joyel.cafe6.engine;

import org.jd.core.v1.api.loader.Loader;

import dev.joyel.cafe6.options.DecompilerOptions;

public interface DecompilerEngine {

    String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception;
}
