package com.github.kwart.jd.engine;

import org.jd.core.v1.api.loader.Loader;

import com.github.kwart.jd.options.DecompilerOptions;

@SuppressWarnings("checkstyle:JavadocType")
public interface DecompilerEngine {

    String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception;
}
