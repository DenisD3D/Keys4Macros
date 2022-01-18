package ml.denisd3d.keys4macros;

public interface IMacro {
    int getKey();
    void setKey(int key);

    int getModifiers();
    void setModifiers(int modifiers);

    String getCommand();
    void setCommand(String command);

    boolean getSend();
    void setSend(boolean send);
}
