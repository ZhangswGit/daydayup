package utils;

/**
 *
 * Utility class for type conversion.
 *
 */
public class TypeUtils {

    /**
     * Helps to avoid using {@code @SuppressWarnings( "unchecked"})} when
     * casting to a generic type.
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }

    public static long asLong(Object value) {
        if (value != null) {
            if (value instanceof Long) {
                return ((Long) value).longValue();
            } else {
                return Long.parseLong(value.toString());
            }
        }
        return 0;
    }

    public static String asString(Object value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumType, String name) {
        return getEnum(enumType, name, null);
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumType, String name, T defaultValue) {
        T value = defaultValue;
        try {
            value = Enum.valueOf(enumType, name);
        } catch (NullPointerException | IllegalArgumentException ex) {
        }
        return value;
    }
}
