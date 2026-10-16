package dev.joyel.0xcafe6.engine;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;

import dev.joyel.0xcafe6.options.DecompilerOptions;
import dev.joyel.0xcafe6.printer.StringBuilderPrinter;

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
