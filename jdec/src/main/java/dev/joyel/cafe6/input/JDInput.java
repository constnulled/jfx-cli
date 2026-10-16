package dev.joyel.cafe6.input;

import dev.joyel.cafe6.JavaDecompiler;
import dev.joyel.cafe6.output.JDOutput;

/**
 * This is an interface for decompiler input plugins.
 *
 * @author Josef Cacek
 */
public interface JDInput {

    void decompile(JavaDecompiler javaDecompiler, JDOutput jdOutput);
}
