package com.github.kwart.jd.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.github.kwart.jd.engine.DecompilerType;

@SuppressWarnings("checkstyle:JavadocType")
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
