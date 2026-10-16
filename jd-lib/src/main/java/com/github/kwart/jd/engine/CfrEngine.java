package com.github.kwart.jd.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.jd.core.v1.api.loader.Loader;

import com.github.kwart.jd.IOUtils;
import com.github.kwart.jd.options.DecompilerOptions;

final class CfrEngine implements DecompilerEngine {

    @Override
    public String decompile(Loader loader, String internalName, DecompilerOptions options) throws Exception {
        byte[] mainBytes = ClassBytes.require(loader, internalName);
        String className = ClassBytes.thisClassName(mainBytes);
        StringBuilder result = new StringBuilder();
        Map<String, String> cfrOptions = new HashMap<>();
        cfrOptions.put("hideutf", String.valueOf(options.isEscapeUnicodeCharacters()));
        cfrOptions.put("showversion", "false");
        CfrDriver driver = new CfrDriver.Builder()
                .withClassFileSource(new LoaderSource(loader, className, mainBytes))
                .withOutputSink(new StringSinkFactory(result))
                .withOptions(cfrOptions)
                .build();
        driver.analyse(Collections.singletonList(className + ".class"));
        return result.toString();
    }

    private static final class LoaderSource implements ClassFileSource {

        private final Loader loader;
        private final String mainClassName;
        private final byte[] mainBytes;

        LoaderSource(Loader loader, String mainClassName, byte[] mainBytes) {
            this.loader = loader;
            this.mainClassName = mainClassName;
            this.mainBytes = mainBytes;
        }

        @Override
        public void informAnalysisRelativePathDetail(String usePath, String specPath) {
        }

        @Override
        public Collection<String> addJar(String jarPath) {
            return Collections.emptyList();
        }

        @Override
        public String getPossiblyRenamedPath(String path) {
            return path;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String path) throws IOException {
            String name = IOUtils.isClassFile(path) ? IOUtils.cutClassSuffix(path) : path;
            if (mainClassName.equals(name)) {
                return Pair.make(mainBytes, path);
            }
            byte[] bytes = ClassBytes.load(loader, name);
            if (bytes == null) {
                bytes = readFromRuntime(path);
            }
            if (bytes == null) {
                throw new IOException("Class not found: " + path);
            }
            return Pair.make(bytes, path);
        }

        private static byte[] readFromRuntime(String path) throws IOException {
            try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
                if (is == null) {
                    return null;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(is, baos);
                return baos.toByteArray();
            }
        }
    }

    private static final class StringSinkFactory implements OutputSinkFactory {

        private final StringBuilder result;

        StringSinkFactory(StringBuilder result) {
            this.result = result;
        }

        @Override
        public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> available) {
            return Collections.singletonList(SinkClass.STRING);
        }

        @Override
        public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
            if (sinkType == SinkType.JAVA && sinkClass == SinkClass.STRING) {
                return sinkable -> result.append(sinkable);
            }
            return sinkable -> {
            };
        }
    }
}
