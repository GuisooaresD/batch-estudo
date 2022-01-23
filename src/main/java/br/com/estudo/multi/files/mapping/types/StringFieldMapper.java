package br.com.estudo.multi.files.mapping.types;

import org.springframework.stereotype.Component;

@Component
public class StringFieldMapper implements FieldMapper<String> {

    @Override
    public String map(final String value) {
        return value;
    }

    @Override
    public Class<String> from() {
        return String.class;
    }

}
