package br.com.estudo.multi.files.mapping;

import br.com.estudo.multi.converters.CallbackFileHeader;
import br.com.estudo.multi.files.mapping.types.FieldMapper;
import br.com.estudo.multi.utils.ReflectionUtils;
import lombok.Builder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static br.com.estudo.multi.utils.ReflectionUtils.newInstance;
import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notEmpty;

public class PropertyFieldLineMapper<T> implements FieldLineMapper<T> {

    private final Class<T> type;
    private final CallbackFileHeader callbackFileHeader;
    private final Map<Class<?>, FieldMapper<?>> mappers;
    private final Map<String, Field> attrMap;
    private final Map<String, Integer> headerOrders;

    @Builder
    public PropertyFieldLineMapper(final Class<T> type, final CallbackFileHeader callbackFileHeader,
                                   final List<FieldMapper<?>> mappers) {
        this.type = type;
        this.callbackFileHeader = callbackFileHeader;
        this.mappers = mappers.stream()
                .map(it -> entry(it.from(), it))
                .collect(toMap(Entry::getKey, Entry::getValue));

        this.attrMap = stream(type.getDeclaredFields())
            .collect(toMap(Field::getName, it -> it));

        headerOrders = newConcurrentMap();
    }

    @Override
    public T mapFieldLine(final String[] line) {
        if (headerOrders.isEmpty()) {
            mapHeader();
        }
        final T obj = newInstance(type);
        final var lines = asList(line);
        attrMap.keySet().forEach(attr -> {
            try {
                final var field = attrMap.get(attr);
                final var lineValue = lines.get(headerOrders.get(attr));
                final var value = mappers.get(field.getType()).map(lineValue);
                ReflectionUtils.setValue(obj, field, value);
            } catch(final Exception e) {
                e.getSuppressed();
            }
        });
        return obj;
    }

    private void mapHeader() {
        final var header = callbackFileHeader.headerNames();
        for (int i = 0; i < header.length; i++) {
            headerOrders.put(header[i], i);
        }
    }
}
