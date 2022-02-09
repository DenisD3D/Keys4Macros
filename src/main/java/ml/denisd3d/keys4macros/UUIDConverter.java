package ml.denisd3d.keys4macros;

import com.electronwill.nightconfig.core.conversion.Converter;

import java.util.UUID;

public class UUIDConverter implements Converter<UUID, String> {
    public UUID convertToField(String value) {
        return value != null ? UUID.fromString(value) : null;
    }

    public String convertFromField(UUID value) {
        return value.toString();
    }
}
