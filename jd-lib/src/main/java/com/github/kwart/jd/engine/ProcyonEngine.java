package com.github.kwart.jd.engine;

import java.io.StringWriter;

import org.jd.core.v1.api.loader.Loader;

import com.github.kwart.jd.options.DecompilerOptions;
import com.strobel.assembler.metadata.*;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;

final class ProcyonEngine implements DecompilerEngine {

    @Override
    public String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception {
        byte[] mainBytes = ClassBytes.require(loader, internalName);
        String className = ClassBytes.thisClassName(mainBytes);

        DecompilerSettings settings = DecompilerSettings.javaDefaults();
        settings.setUnicodeOutputEnabled(!options.isEscapeUnicodeCharacters());
        settings.setShowDebugLineNumbers(options.isDisplayLineNumbers());

        MetadataSystem metadata = new MetadataSystem(
                new CompositeTypeLoader(new LoaderTypeLoader(loader, className, mainBytes), new ClasspathTypeLoader()));
        TypeReference reference = metadata.lookupType(className);
        TypeDefinition definition = reference == null ? null : reference.resolve();
        if (definition == null) {
            throw new IllegalStateException("Procyon can't resolve type " + className);
        }

        DecompilationOptions decompilationOptions = new DecompilationOptions();
        decompilationOptions.setSettings(settings);
        decompilationOptions.setFullDecompilation(true);

        StringWriter writer = new StringWriter();
        settings.getLanguage().decompileType(definition, new PlainTextOutput(writer), decompilationOptions);
        return writer.toString();
    }

    private static final class LoaderTypeLoader implements ITypeLoader {

        private final Loader loader;
        private final String mainClassName;
        private final byte[] mainBytes;

        LoaderTypeLoader(Loader loader, String mainClassName, byte[] mainBytes) {
            this.loader = loader;
            this.mainClassName = mainClassName;
            this.mainBytes = mainBytes;
        }

        @Override
        public boolean tryLoadType(String internalName, Buffer buffer) {
            byte[] bytes = mainClassName.equals(internalName) ? mainBytes : ClassBytes.load(loader, internalName);
            if (bytes == null) {
                return false;
            }
            buffer.reset(bytes.length);
            buffer.putByteArray(bytes, 0, bytes.length);
            buffer.position(0);
            return true;
        }
    }
}
