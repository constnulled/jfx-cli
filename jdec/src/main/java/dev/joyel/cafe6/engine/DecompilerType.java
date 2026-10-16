package dev.joyel.cafe6.engine;

import java.util.Locale;

public enum DecompilerType {

    JD("jd"),
    CFR("cfr"),
    FERNFLOWER("fernflower"),
    PROCYON("procyon");

    private final String cliName;

    DecompilerType(String cliName) {
        this.cliName = cliName;
    }

    public String getCliName() {
        return cliName;
    }

    public DecompilerEngine createEngine() {
        switch (this) {
            case CFR:
                return new CfrEngine();
            case FERNFLOWER:
                return new FernflowerEngine();
            case PROCYON:
                return new ProcyonEngine();
            case JD:
            default:
                return new JdCoreEngine();
        }
    }

    public static DecompilerType fromName(String name) {
        if (name != null) {
            String normalized = name.trim().toLowerCase(Locale.ROOT);
            for (DecompilerType type : values()) {
                if (type.cliName.equals(normalized)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Unknown decompiler '" + name + "'. Supported: " + supportedNames());
    }

    public static String supportedNames() {
        StringBuilder sb = new StringBuilder();
        for (DecompilerType type : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(type.cliName);
        }
        return sb.toString();
    }
}
