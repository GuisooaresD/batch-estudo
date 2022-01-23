package br.com.estudo.multi.files.mapping.types;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import static java.lang.Integer.valueOf;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class IntegerFieldMapper implements FieldMapper<Integer> {

    @Override
    public Integer map(final String value) {
        if (isBlank(value)) {
            return null;
        }
        return valueOf(value);
    }

    @Override
    public Class<Integer> from() {
        return Integer.class;
    }

}
