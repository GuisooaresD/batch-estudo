package br.com.estudo.multi.files.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

public class JsonFieldLineMapper<T> implements FieldLineMapper<T> {

    private final ObjectMapper mapper;
    private final Class<T> type;

    @Builder
    public JsonFieldLineMapper(final Class<T> type) {
        this.type = type;
        mapper = new ObjectMapper();
    }

    @Builder
    public JsonFieldLineMapper(final ObjectMapper mapper, final Class<T> type) {
        this.mapper = mapper;
        this.type = type;
    }

    @Override
    public T mapFieldLine(final String line) {
        try {
            return mapper.readValue(line, type);
        } catch (final JsonProcessingException e) {
            System.out.printf("Cannot parse %s to %s\n", type.getSimpleName(), line);
            return null;
        }
    }
}
