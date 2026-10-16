package dev.joyel.cafe6.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.joyel.cafe6.IOUtils;
import dev.joyel.cafe6.JavaDecompiler;
import dev.joyel.cafe6.loader.FileLoader;
import dev.joyel.cafe6.output.JDOutput;

/**
 * {@link JDInput} implementation which takes a single class file as an input.
 *
 * @author Josef Cacek
 */
public class ClassFileInput extends AbstractFileJDInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassFileInput.class);

    public ClassFileInput(String path) {
        super(path);
    }

    public ClassFileInput(String filePath, String pattern) throws IllegalArgumentException {
        super(filePath, pattern);
    }

    /*
     * (non-Javadoc)
     *
     * @see jd.core.input.JDInput#decompile(jd.core.JavaDecompiler, jd.core.output.JDOutput)
     */
    @Override
    public void decompile(JavaDecompiler javaDecompiler, JDOutput jdOutput) {
        if (javaDecompiler == null || jdOutput == null) {
            LOGGER.warn("Decompiler or JDOutput are null");
            return;
        }

        final String name = file.getName();
        if (skipThePath(name)) {
            return;
        }
        jdOutput.init(javaDecompiler.getOptions(), "");
        LOGGER.debug("Decompiling single class file {}", name);
        String nameWithoutClassSfx = IOUtils.isClassFile(name) ? IOUtils.cutClassSuffix(name) : name;
        jdOutput.processClass(nameWithoutClassSfx, javaDecompiler.decompileClass(new FileLoader(), file.getPath()));
        jdOutput.commit();
    }
}
