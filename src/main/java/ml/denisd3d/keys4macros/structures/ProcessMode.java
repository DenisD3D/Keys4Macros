package ml.denisd3d.keys4macros.structures;

public enum ProcessMode {
    UNKNOWN,
    SEND,
    WRITE;

    private static final ProcessMode[] vals = {SEND, WRITE}; // Avoid array copying at each next call
    public ProcessMode next()
    {
        return vals[(this.ordinal()) % (vals.length)];
    }
}
