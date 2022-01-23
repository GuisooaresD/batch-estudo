package br.com.estudo.multi.files.mapping;

import br.com.estudo.multi.files.mapping.callback.CallbackFileHeader;
import br.com.estudo.multi.files.mapping.types.FieldMapper;
import lombok.Builder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static br.com.estudo.multi.utils.ReflectionUtils.newInstance;
import static br.com.estudo.multi.utils.ReflectionUtils.setValue;
import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public class HeaderFieldLineMapper<T> implements FieldLineMapper<T> {

    private final Class<T> type;
    private final CallbackFileHeader callbackFileHeader;
    private final Map<Class<?>, FieldMapper<?>> mappers;
    private final Map<String, Field> attrMap;
    private final Map<String, Integer> headerOrders;
    private final String delimiter;

    @Builder
    public HeaderFieldLineMapper(final Class<T> type, final CallbackFileHeader callbackFileHeader,
                                 final List<FieldMapper<?>> mappers, final String delimiter) {
        this.type = type;
        this.callbackFileHeader = callbackFileHeader;
        this.delimiter = delimiter;
        this.mappers = mappers.stream()
                .collect(toMap(FieldMapper::from, it -> it));

        this.attrMap = stream(type.getDeclaredFields())
                .collect(toMap(Field::getName, it -> it));

        headerOrders = newConcurrentMap();
    }

    @Override
    public T mapFieldLine(final String line) {
        if (headerOrders.isEmpty()) {
            mapHeader();
        }
        final T obj = newInstance(type);
        final var lines = asList(line.split(delimiter));
        attrMap.keySet().forEach(attr -> {
            final var lineValue = lines.get(headerOrders.get(attr));
            final var field = attrMap.get(attr);
            final var value = mappers.get(field.getType()).map(lineValue);
            try {
                setValue(obj, field, value);
            } catch (final Exception e) {
                System.out.printf("value %s cannot be set in field %s\n", value, field.getName());
            }
        });
        return obj;
    }

    private void mapHeader() {
        final var header = callbackFileHeader.fileHeader().split(delimiter);
        for (int i = 0; i < header.length; i++) {
            headerOrders.put(header[i], i);
        }
    }
}
