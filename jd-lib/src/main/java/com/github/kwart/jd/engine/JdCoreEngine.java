package com.github.kwart.jd.engine;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;

import com.github.kwart.jd.options.DecompilerOptions;
import com.github.kwart.jd.printer.StringBuilderPrinter;

final class JdCoreEngine implements DecompilerEngine {

    private static final ThreadLocal<ClassFileToJavaSourceDecompiler> DECOMPILER_TL = ThreadLocal
            .withInitial(() -> new ClassFileToJavaSourceDecompiler());

    @Override
    public String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception {
        StringBuilderPrinter printer = new StringBuilderPrinter(options);
        DECOMPILER_TL.get().decompile(loader, printer, internalName);
        return printer.toString();
    }
}
