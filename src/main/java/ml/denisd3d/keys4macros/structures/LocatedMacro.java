package ml.denisd3d.keys4macros.structures;

import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import ml.denisd3d.keys4macros.UUIDConverter;

import java.util.UUID;

@PreserveNotNull
public class LocatedMacro implements IMacro {
    @Path("id")
    @Conversion(UUIDConverter.class)
    UUID id = UUID.randomUUID();

    @Path("command")
    String command = "";

    @Path("key")
    int key = -1;

    @Path("modifier")
    int modifiers = 0;

    @Path("location")
    String location = "";

    @Path("process_mode")
    ProcessMode mode = ProcessMode.SEND;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

