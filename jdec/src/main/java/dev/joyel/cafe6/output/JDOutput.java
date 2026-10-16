package dev.joyel.cafe6.output;

import java.io.InputStream;

import dev.joyel.cafe6.options.DecompilerOptions;

public interface JDOutput {

    /**
     * Initialize the plugin for the given input basePath. Resources can be reserved here.
     *
     * @param basePath path to decompiled resource(s) (e.g. directory, jar file, ...)
     */
    void init(DecompilerOptions options, String basePath);


    void processClass(String classPath, String decompiledSrc);

    /**
     * Handle resource from
     *
     * @param filePath a path to a resource within a basePath (e.g. "org/myproject/translations/messages_en.properties")
     * @param is the resource input stream
     */
    void processResource(String filePath, InputStream is);

    /**
     * Finish the processing. Resources should be released in this method.
     */
    void commit();
}
