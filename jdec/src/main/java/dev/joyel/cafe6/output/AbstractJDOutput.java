
package dev.joyel.cafe6.output;

import dev.joyel.cafe6.options.DecompilerOptions;

/**
 * Abstract parent with more or less empty implementation of {@link #init(String)} and {@link #commit()} methods.
 */
public abstract class AbstractJDOutput implements JDOutput {

    protected volatile DecompilerOptions options;

    public void init(DecompilerOptions options, String basePath) {
        this.options = options;
    }

    public void commit() {
    }

}
