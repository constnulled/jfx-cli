package dev.joyel.cafe6.engine;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jd.core.v1.api.loader.Loader;

final class ClassBytes {

    private static final int CP_UTF8 = 1;
    private static final int CP_LONG = 5;
    private static final int CP_DOUBLE = 6;
    private static final int CP_CLASS = 7;

    private ClassBytes() {
    }

    static byte[] load(Loader loader, String name) {
        if (loader == null || name == null) {
            return null;
        }
        try {
            return loader.canLoad(name) ? loader.load(name) : null;
        } catch (IOException e) {
            return null;
        }
    }

    static byte[] require(Loader loader, String name) throws IOException {
        byte[] bytes = load(loader, name);
        if (bytes == null) {
            throw new IOException("Class not found: " + name);
        }
        return bytes;
    }

    static String thisClassName(byte[] bytes) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            in.readInt();
            in.readUnsignedShort();
            in.readUnsignedShort();
            int count = in.readUnsignedShort();
            String[] utf8 = new String[count];
            int[] classNameIndex = new int[count];
            for (int i = 1; i < count; i++) {
                int tag = in.readUnsignedByte();
                switch (tag) {
                    case CP_UTF8:
                        utf8[i] = in.readUTF();
                        break;
                    case CP_CLASS:
                        classNameIndex[i] = in.readUnsignedShort();
                        break;
                    case CP_LONG:
                    case CP_DOUBLE:
                        in.skipBytes(8);
                        i++;
                        break;
                    default:
                        in.skipBytes(skipLength(tag));
                        break;
                }
            }
            in.readUnsignedShort();
            return utf8[classNameIndex[in.readUnsignedShort()]];
        }
    }

    private static int skipLength(int tag) throws IOException {
        switch (tag) {
            case 3:
            case 4:
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 18:
                return 4;
            case 8:
            case 16:
            case 19:
            case 20:
                return 2;
            case 15:
                return 3;
            default:
                throw new IOException("Unsupported constant pool tag " + tag);
        }
    }

    static Map<String, byte[]> withInnerClasses(Loader loader, String className, byte[] mainBytes) {
        Map<String, byte[]> result = new LinkedHashMap<>();
        result.put(className, mainBytes);
        Pattern inner = Pattern.compile(Pattern.quote(className) + "\\$[\\p{Alnum}_$]+");
        Deque<byte[]> queue = new ArrayDeque<>();
        queue.add(mainBytes);
        while (!queue.isEmpty()) {
            Matcher matcher = inner.matcher(new String(queue.poll(), StandardCharsets.ISO_8859_1));
            while (matcher.find()) {
                String name = matcher.group();
                if (!result.containsKey(name)) {
                    byte[] bytes = load(loader, name);
                    if (bytes != null) {
                        result.put(name, bytes);
                        queue.add(bytes);
                    }
                }
            }
        }
        return result;
    }
}
