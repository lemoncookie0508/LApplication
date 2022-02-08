package lepl;

public record BaseType(int baseType) {
    public static final int EMPTY = 0;
    public static final int EXIT = 1;
    public static final int MAXIMIZE = 2;
    public static final int MINIMIZE = 4;

    public static final int DEFAULT = EXIT | MAXIMIZE | MINIMIZE;
}
