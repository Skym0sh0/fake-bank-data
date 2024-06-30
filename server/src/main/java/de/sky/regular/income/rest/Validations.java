package de.sky.regular.income.rest;

import java.util.Objects;
import java.util.UUID;

public abstract class Validations {
    public static <T> T requireNonNull(T obj, String name) {
        if (obj == null)
            throw new IllegalArgumentException(String.format("Null is not allowed for '%s'", name));

        return obj;
    }

    public static UUID requireNonNullAndEquality(UUID a, UUID b) {
        if (!Objects.equals(a, b))
            throw new IllegalArgumentException(String.format("IDs must match %s != %b", a, b));

        return a;
    }
}
