package dev.joyel.cafe6.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import dev.joyel.cafe6.engine.DecompilerType;

public class DecompilerTypeConverter implements IStringConverter<DecompilerType> {

    @Override
    public DecompilerType convert(String value) {
        try {
            return DecompilerType.fromName(value);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(e.getMessage());
        }
    }
}
