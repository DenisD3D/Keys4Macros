package ml.denisd3d.keys4macros.structures;

import java.util.UUID;

public interface IMacro {
    int getKey();

    void setKey(int key);

    int getModifiers();

    void setModifiers(int modifiers);

    String getCommand();

    void setCommand(String command);

    ProcessMode getMode();

    void setMode(ProcessMode mode);

    String getLocation();

    void setLocation(String location);

    default boolean isLocked() {
        return false;
    }

    default boolean isComplete() {
        return true;
    }

    default void setComplete(boolean complete) {

    }

    default int getDefaultKey() {
        return -1;
    }

    default int getDefaultModifiers() {
        return 0;
    }

    UUID getId();

    void setId(UUID id);
}