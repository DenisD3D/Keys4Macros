package ml.denisd3d.keys4macros.structures;

import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import ml.denisd3d.keys4macros.UUIDConverter;

import java.util.UUID;

@PreserveNotNull
public class ForcedMacro implements IMacro {
    @Path("id")
    @Conversion(UUIDConverter.class)
    UUID id = UUID.randomUUID();

    @Path("key")
    int key = -1;

    @Path("modifiers")
    int modifiers = 0;

    @Path("location")
    String location = "";

    transient int default_key = -1;
    transient int default_modifiers = 0;
    transient String command = "??";
    transient ProcessMode mode = ProcessMode.UNKNOWN;
    transient boolean complete = false;

    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public String getCommand() {
        return this.command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public ProcessMode getMode() {
        return this.mode;
    }

    @Override
    public void setMode(ProcessMode mode) {
        this.mode = mode;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean isLocked() {
        return true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public int getDefaultKey() {
        return this.default_key;
    }

    public void setDefaultKey(int default_key) {
        this.default_key = default_key;
    }

    @Override
    public int getDefaultModifiers() {
        return this.default_modifiers;
    }

    public void setDefaultModifiers(int default_modifiers) {
        this.default_modifiers = default_modifiers;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}