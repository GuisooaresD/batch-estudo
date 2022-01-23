package br.com.estudo.multi.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.StringUtils.capitalize;

@NoArgsConstructor(access = PRIVATE)
public final class ReflectionUtils {

    @SneakyThrows
    public static void setValue(final Object target, final Field field, final Object value) {
        final var methodName = "set" + capitalize(field.getName());
        final var method = target.getClass().getDeclaredMethod(methodName, value.getClass());
        if (!isNull(method)) {
            method.invoke(target, value);
        }
    }

    @SneakyThrows
    public static <T> T newInstance(final Class<T> type) {
        return type.getConstructor().newInstance();
    }

}
