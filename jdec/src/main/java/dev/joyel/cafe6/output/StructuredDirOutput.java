package dev.joyel.cafe6.output;

import java.io.File;

import dev.joyel.cafe6.options.DecompilerOptions;

/**
 * {@link JDOutput} implementation, which stores decompiled classes to a subdirectory of the output directory. The subdirectory
 * has the same name as the source file or directory. When the source doesn't contain a file name, then {@value #NO_SRC_NAME} is
 * used as the output subdirectory name.
 *
 * @author Josef Cacek
 */
public class StructuredDirOutput extends DirOutput {

    /**
     * Subdirectory name used for output when the decompiler input doesn't come as a file.
     */
    public static final String NO_SRC_NAME = "none";

    private volatile File targetDir;

    /**
     * Constructor which takes directory path as a parameter.
     *
     * @param outputDir base directory path to save output. Must not be {@code null}
     */
    public StructuredDirOutput(File outputDir) {
        super(outputDir);
    }

    @Override
    public void init(DecompilerOptions options, String basePath) {
        super.init(options, basePath);
        if (null == basePath || basePath.isEmpty()) {
            targetDir = new File(dir, "none");
        } else {
            File tmpFile = new File(basePath);
            targetDir = new File(dir, tmpFile.getName());
        }
    }

    @Override
    protected File getTargetDir() {
        return targetDir;
    }
}
