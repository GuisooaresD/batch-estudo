package br.com.estudo.multi.readers.core;

import br.com.estudo.multi.files.mapping.FieldLineMapper;
import lombok.Builder;
import lombok.Setter;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.core.io.Resource;

import static java.util.Objects.isNull;

@Setter
public class FieldLineItemReader<T> extends AbstractLineItemReader<T> {

    private final FieldLineMapper<T> fieldLineMapper;

    @Builder
    public FieldLineItemReader(final Resource resource, final LineCallbackHandler lineCallbackHandler,
                               final FieldLineMapper<T> fieldLineMapper) {
        super(resource, lineCallbackHandler);
        this.fieldLineMapper = fieldLineMapper;
    }

    @Override
    public T next() {
        final var line = nextLine();
        if (isNull(line)) {
            return null;
        }
        return fieldLineMapper.mapFieldLine(line);
    }
}
