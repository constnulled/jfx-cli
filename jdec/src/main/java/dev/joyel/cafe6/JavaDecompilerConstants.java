package dev.joyel.cafe6;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Constants used by the library classes.
 *
 * @author Josef Cacek
 */
public final class JavaDecompilerConstants {

    /**
     * Project version (loaded from pom.properties in META-INF directory).
     */
    public static final String VERSION;

    /**
     * Temp directory configured in java.io.tmpdir.
     */
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    /**
     * Line separator.
     */
    public static final String LS = System.getProperty("line.separator");

    /**
     * Filename suffix for compiled classes.
     */
    public static final String CLASS_SUFFIX = ".class";
    /**
     * Length of {@link #CLASS_SUFFIX}.
     */
    public static final int CLASS_SUFFIX_LEN = CLASS_SUFFIX.length();
    /**
     * Filename suffix for java source files.
     */
    public static final String JAVA_SUFFIX = ".java";

    /**
     * First bytes (magic number) in class files.
     */
    public static final int MAGIC_NR_CLASS_FILE = 0xCAFEBABE;
    /**
     * First bytes (magic number) in zip files.
     */
    public static final int MAGIC_NR_ZIP_FILE = 0x504B0304;

    private JavaDecompilerConstants() {
    }

    static {
        String version = "[UNKNOWN]";
        try (InputStream is = JavaDecompilerConstants.class
                .getResourceAsStream("/META-INF/maven/dev.joyel.cafe6/jd-cli/pom.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                if (props.containsKey("version")) {
                    version = props.getProperty("version");
                }
            }
        } catch (IOException e) {
            // ignore
        }
        VERSION = version;
    }
}
