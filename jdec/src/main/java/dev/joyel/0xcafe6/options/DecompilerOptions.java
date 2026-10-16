package dev.joyel.0xcafe6.options;

import dev.joyel.0xcafe6.engine.DecompilerType;

/**
 * Interface representing decompiler options.
 *
 * @author Josef Cacek
 */
public interface DecompilerOptions {

    boolean isSkipResources();

    boolean isDisplayLineNumbers();

    boolean isEscapeUnicodeCharacters();

    boolean isParallelProcessingAllowed();

    default DecompilerType getDecompilerType() {
        return DecompilerType.JD;
    }
}
