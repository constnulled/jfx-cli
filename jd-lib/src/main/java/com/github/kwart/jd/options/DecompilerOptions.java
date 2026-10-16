package com.github.kwart.jd.options;

import com.github.kwart.jd.engine.DecompilerType;

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
